/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.codegen;

import org.jboss.errai.codegen.control.branch.Label;
import org.jboss.errai.codegen.control.branch.LabelReference;
import org.jboss.errai.codegen.exception.OutOfScopeException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.util.GenUtil;
import org.jboss.errai.common.client.framework.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents a context in which {@link Statement}s are generated.
 * <p>
 * Its main purpose is to support the concept of scopes so that {@link Statement}s can be validated prior to
 * compilation.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class Context {
  private Context parent = null;

  private Map<String, Variable> variables;
  private Map<String, Label> labels;

  private boolean autoImportActive = false;
  private Map<String, String> imports;
  private final Set<MetaClass> classContexts;

  private final Set<MetaClass> literalizableClasses;

  private Map<String, Map<Object, Object>> renderingCache;

  private Context() {
    classContexts = new HashSet<MetaClass>();
    renderingCache = new HashMap<String, Map<Object, Object>>();
    literalizableClasses = new HashSet<MetaClass>();
  }

  private Context(final Context parent) {
    this();
    this.parent = parent;
    this.autoImportActive = parent.autoImportActive;
    this.imports = parent.imports;
    this.renderingCache = parent.renderingCache;
  }

  /**
   * Creates a new and empty context.
   * 
   * @return empty context
   */
  public static Context create() {
    return new Context();
  }

  /**
   * Create a new sub context for the given parent context.
   * 
   * @param parent
   *          the parent context to use.
   * 
   * @return Created sub context
   */
  public static Context create(final Context parent) {
    return new Context(parent);
  }

  /**
   * Add a variable to the current scope.
   * 
   * @param name
   *          the name of the variable, must not be null.
   * @param type
   *          the type of the variable, must not be null.
   * @return the current context with the variable added.
   */
  public Context addVariable(final String name, final Class<?> type) {
    return addVariable(Variable.create(Assert.notNull(name), Assert.notNull(type)));
  }

  /**
   * Add a variable to the current scope and initialize it.
   * 
   * @param name
   *          the name of the variable, must not be null.
   * @param type
   *          the type of the variable, must not be null.
   * @param initialization
   *          the {@link Statement} or literal value to initialize the {@link Variable}, can be null.
   * @return the current context with the variable added.
   */
  public Context addVariable(final String name, final Class<?> type, final Object initialization) {
    final Variable v = Variable.create(Assert.notNull(name), Assert.notNull(type), initialization);
    return addVariable(v);
  }

  /**
   * Add a {@link Variable} to the current scope.
   * 
   * @param variable
   *          the variable instance to add, must not be null.
   * @return the current context with the variable added.
   */
  public Context addVariable(final Variable variable) {
    if (variables == null)
      variables = new HashMap<String, Variable>();

    variables.put(variable.getName(), variable);
    return this;
  }

  /**
   * Add a {@link Label} to the current scope.
   * 
   * @param label
   *          the label instance to add, must not be null.
   * @return the current context with the label added.
   */
  public Context addLabel(final Label label) {
    if (labels == null)
      labels = new HashMap<String, Label>();

    labels.put(label.getName(), label);
    return this;
  }

  // Ensures that imports are visible/shared between all parent/child contexts.
  private void initImports() {
    if (imports == null) {
      Context c = this;
      Map<String, String> importsMap = null;

      while (c.parent != null) {
        c = c.parent;
        if (c.imports != null)
          importsMap = c.imports;
      }

      if (importsMap == null) {
        imports = importsMap = new HashMap<String, String>();

        c = this;
        while (c.parent != null) {
          (c = c.parent).imports = importsMap;
        }
      }
      else {
        imports = importsMap;
      }
    }
  }

  /**
   * Imports the given class.
   * 
   * @param clazz
   *          the class to import, must not be null. If it is an array type (of any number of dimensions), its non-array
   *          component type will be imported.
   * 
   * @return the current context with the import added.
   */
  public Context addImport(MetaClass clazz) {
    initImports();

    while (clazz.isArray()) {
      clazz = clazz.getComponentType();
    }

    if (!imports.containsKey(clazz.getName())) {
      final String imp = getImportForClass(clazz);
      if (imp != null) {
        imports.put(clazz.getName(), imp);
      }
    }

    return this;
  }

  /**
   * Checks whether the given class has been imported.
   * 
   * @param clazz
   *          the class to check, must not be null.
   * @return true if import exists, otherwise false.
   */
  public boolean hasImport(MetaClass clazz) {
    if (clazz.isArray()) {
      clazz = clazz.getComponentType();
    }

    return imports != null && imports.containsKey(clazz.getName()) &&
        imports.get(clazz.getName()).equals(getImportForClass(clazz));
  }

  private String getImportForClass(final MetaClass clazz) {
    String imp = null;

    final String fqcn = clazz.getCanonicalName();
    final int idx = fqcn.lastIndexOf('.');
    if (idx != -1) {
      imp = fqcn.substring(0, idx);
    }

    return imp;
  }

  /**
   * Returns all imports except the optional ones (java.lang.*).
   * 
   * @return required imports
   */
  public Set<String> getRequiredImports() {
    if (imports == null)
      return Collections.emptySet();

    final Set<String> importedClasses = new TreeSet<String>();
    for (final String className : imports.keySet()) {
      final String packageName = imports.get(className);
      if (!packageName.equals("java.lang")) {
        importedClasses.add(packageName + "." + className);
      }
    }
    return importedClasses;
  }

  /**
   * Enables automatic import of classes used during code generation.
   * 
   * @return the current context whit auto import enabled.
   */
  public Context autoImport() {
    this.autoImportActive = true;
    return this;
  }

  /**
   * Returns a reference to the {@link Variable} with the given name.
   * 
   * @param name
   *          the name of the variable.
   * @return the {@link VariableReference} found, can not be null.
   * @throws OutOfScopeException
   *           if variable with the given name can not be found.
   */
  public VariableReference getVariable(final String name) {
    return getVariable(name, false);
  }

  /**
   * Returns a reference to the class member {@link Variable} with the given name.
   * 
   * @param name
   *          the name of the class member variable.
   * @return the {@link VariableReference} found, can not be null.
   * @throws OutOfScopeException
   *           if member variable with the given name can not be found.
   */
  public VariableReference getClassMember(final String name) {
    return getVariable(name, true);
  }

  private VariableReference getVariable(final String name, final boolean mustBeClassMember) {
    Variable found = null;
    Context ctx = this;
    do {
      if (ctx.variables != null) {
        final Variable var = ctx.variables.get(name);
        found = (mustBeClassMember && var != null && !variables.containsKey(var.getName())) ? null : var;
      }
    }
    while (found == null && (ctx = ctx.parent) != null);

    if (found == null) {
      if (GenUtil.isPermissiveMode()) {
        return Variable.create(name, Object.class).getReference();
      }
      else {
        throw new OutOfScopeException((mustBeClassMember) ? "this." + name : name + " not found.\nScope:\n" + this);
      }
    }

    return found.getReference();
  }

  /**
   * Returns the a reference to the {@link Label} with the given name.
   * 
   * @param name
   *          the name of the label.
   * @return the {@link LabelReference} found, can not be null.
   * @throws OutOfScopeException
   *           if label with the given name can not be found.
   */
  public LabelReference getLabel(final String name) {
    Label found = null;
    Context ctx = this;
    do {
      if (ctx.labels != null) {
        found = ctx.labels.get(name);
      }
    }
    while (found == null && (ctx = ctx.parent) != null);

    if (found == null)
      throw new OutOfScopeException("Label not found: " + name);

    return found.getReference();
  }

  /**
   * Checks is the given {@link Variable} is in scope.
   * 
   * @param variable
   *          the variable to check.
   * @return true if in scope, otherwise false.
   */
  public boolean isScoped(final Variable variable) {
    Context ctx = this;
    do {
      if (ctx.variables != null && ctx.variables.containsValue(variable))
        return true;
    }
    while ((ctx = ctx.parent) != null);
    return false;
  }

  /**
   * Checks is the given {@link MetaMethod} is in scope (part of the attached class contexts).
   * 
   * @param method
   *          the method to check.
   * @return true if in scope, otherwise false.
   */
  public boolean isInScope(final MetaMethod method) {
    Context c = this;
    do {
      for (final MetaClass clazz : c.classContexts) {
        for (final MetaMethod m : clazz.getDeclaredMethods()) {
          if (m.equals(method))
            return true;
        }
      }
    }
    while ((c = c.parent) != null);

    return false;
  }

  /**
   * Checks is the given {@link MetaField} is in scope (part of the attached class contexts).
   * 
   * @param field
   *          the field to check.
   * @return true if in scope, otherwise false.
   */
  public boolean isInScope(final MetaField field) {
    Context c = this;
    do {
      for (final MetaClass clazz : c.classContexts) {
        for (final MetaField m : clazz.getDeclaredFields()) {
          if (m.equals(field))
            return true;
        }
      }
    }
    while ((c = c.parent) != null);

    return false;
  }

  /**
   * Checks if the the given variable name is ambiguous in this scope.
   * 
   * @param varName
   *          the variable name to check.
   * @return true if ambiguous, otherwise false.
   */
  public boolean isAmbiguous(final String varName) {
    Context ctx = this;
    int matches = 0;
    do {
      if (ctx.variables != null && ctx.variables.containsKey(varName))
        matches++;
    }
    while ((ctx = ctx.parent) != null);
    return matches > 1;
  }

  /**
   * Returns all variables in this scope (does not include variables of parent scopes).
   * 
   * @return collection of {@link Variable}, empty if no variables are in scope.
   */
  public Collection<Variable> getDeclaredVariables() {
    if (variables == null)
      return Collections.<Variable> emptyList();
    return variables.values();
  }

  public void addLiteralizableClasses(final Collection<Class<?>> clazzes) {
    for (final Class<?> cls : clazzes) {
      addLiteralizableClass(cls);
    }
  }

  public void addLiteralizableMetaClasses(final Collection<MetaClass> clazzes) {
    for (final MetaClass cls : clazzes) {
      addLiteralizableClass(cls);
    }
  }

  /**
   * Mark a class "literalizable". Meaning that all classes that are assignable to this type, are candidates for
   * reification to code snapshots for this context and all subcontexts. See {@link SnapshotMaker} for further details.
   * 
   * @param clazz
   *          the class, interface or superclass to be considered literalizable.
   */
  public void addLiteralizableClass(final Class clazz) {
    addLiteralizableClass(MetaClassFactory.get(clazz));
  }

  /**
   * Mark a class "literalizable". Meaning that all classes that are assignable to this type, are candidates for
   * reification to code snapshots for this context and all subcontexts. See {@link SnapshotMaker} for further details.
   * 
   * @param clazz
   *          the class, interface or superclass to be considered literalizable.
   */
  public void addLiteralizableClass(final MetaClass clazz) {
    literalizableClasses.add(clazz.getErased());
  }

  /**
   * Returns true if the specified class is literalizable.
   * 
   * @see #addLiteralizableClass(Class)
   * @param clazz
   *          the class, interface or superclass to be tested if literalizable
   * @return true if the specified class is literalizable
   */
  public boolean isLiteralizableClass(final Class clazz) {
    return isLiteralizableClass(MetaClassFactory.get(clazz));
  }

  /**
   * Returns true if the specified class is literalizable.
   * 
   * @see #addLiteralizableClass(org.jboss.errai.codegen.meta.MetaClass)
   * @param clazz
   *          the class, interface or superclass to be tested if literalizable
   * @return true if the specified class is literalizable
   */
  public boolean isLiteralizableClass(final MetaClass clazz) {
    return getLiteralizableTargetType(clazz) != null;
  }

  /**
   * Returns the literalizable target type for any matching subtype. Meaning, that if say, the type
   * <tt com.bar.FooImpl</tt> is a subtype of the interface <tt>com.bar.Foo</tt>, which is itself marked literalizable,
   * this method will return a reference to the <tt>java.lang.Class</tt> instance for <tt>com.bar.Foo</tt>
   * 
   * @param clazz
   *          the class, interface or superclass to obtain a literalizable target type for.
   * @return the literalizable target type that matches
   * @param clazz
   *          . If there are no matches, returns <tt>null</tt>.
   */
  public Class getLiteralizableTargetType(final Class clazz) {
    return getLiteralizableTargetType(MetaClassFactory.get(clazz));
  }

  /**
   * Returns the literalizable target type for any matching subtype. Meaning, that if say, the type
   * <tt>com.bar.FooImpl</tt> is a subtype of the interface <tt>com.bar.Foo</tt>, which is itself marked literalizable,
   * this method will return a reference to the <tt>java.lang.Class</tt> instance for <tt>com.bar.Foo</tt>
   * 
   * @param clazz
   *          the class, interface or superclass to obtain a literalizable target type for.
   * @return the literalizable target type that matches
   * @param clazz
   *          . If there are no matches, returns <tt>null</tt>.
   */
  public Class getLiteralizableTargetType(final MetaClass clazz) {
    Context ctx = this;
    do {
      MetaClass cls = clazz;
      do {
        if (ctx.literalizableClasses.contains(cls))
          return cls.asClass();

        for (final MetaClass iface : cls.getInterfaces()) {
          if (ctx.literalizableClasses.contains(iface))
            return iface.asClass();
        }
      }
      while ((cls = cls.getSuperClass()) != null);
    }
    while ((ctx = ctx.parent) != null);

    return null;
  }

  /**
   * Returns all variables in this scope (does not include variables of parent scopes).
   * 
   * @return map of variable name to {@link Variable}, empty if no variables are in scope.
   */
  public Map<String, Variable> getVariables() {
    if (variables == null)
      return Collections.<String, Variable> emptyMap();

    return Collections.<String, Variable> unmodifiableMap(variables);
  }

  /**
   * Attaches a class to the current scope.
   * 
   * @param clazz
   *          class to attach.
   */
  public void attachClass(final MetaClass clazz) {
    this.classContexts.add(clazz);
  }

  /**
   * Checks if automatic import is active.
   * 
   * @return true if auto import active, otherwise false.
   */
  public boolean isAutoImportActive() {
    return autoImportActive;
  }

  // TODO factor this out. should not be part of Context.
  public <K, V> Map<K, V> getRenderingCache(final RenderCacheStore<K, V> store) {
    Map<K, V> cacheStore = (Map<K, V>) renderingCache.get(store.getName());
    if (cacheStore == null) {
      renderingCache.put(store.getName(), (Map<Object, Object>) (cacheStore = new HashMap<K, V>()));
    }
    return cacheStore;
  }

  @Override
  public String toString() {
    String indent = "";
    final StringBuilder context = new StringBuilder();

    Context ctx = this;
    do {
      if (ctx.variables != null && !ctx.variables.isEmpty()) {
        context.append("Variables:\n");
        for (final String varName : ctx.variables.keySet()) {
          context.append(indent).append(ctx.variables.get(varName)).append("\n");
        }
      }

      if (ctx.labels != null && !ctx.labels.isEmpty()) {
        context.append("Labels:\n");
        for (final String labelName : ctx.labels.keySet()) {
          context.append(indent).append(ctx.labels.get(labelName)).append("\n");
        }
      }

      if (ctx.classContexts != null && !ctx.classContexts.isEmpty()) {
        context.append("Classes:\n");
        for (final MetaClass clazz : ctx.classContexts) {
          context.append(indent).append(clazz.getFullyQualifiedName()).append("\n");
        }
      }

      if (ctx.imports != null && !ctx.imports.isEmpty()) {
        context.append("Imports:\n");
        for (final String className : ctx.imports.keySet()) {
          context.append(indent).append(ctx.imports.get(className)).append(".").append(className).append("\n");
          ;
        }
      }

      indent += "  ";
    }
    while ((ctx = ctx.parent) != null);

    return context.toString();
  }
}