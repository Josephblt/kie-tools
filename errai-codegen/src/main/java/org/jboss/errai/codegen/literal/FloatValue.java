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

package org.jboss.errai.codegen.literal;

import org.jboss.errai.codegen.Context;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class FloatValue extends LiteralValue<Float> {

  public FloatValue(final Float value) {
    super(value);
  }

  @Override
  public String getCanonicalString(final Context context) {
    return getValue().toString() + "f";
  }
}
