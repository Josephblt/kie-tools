/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.DataObjectReference;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseRootProcessAdvancedData;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * An Abstract Class to Handle Data Type Cache.
 */
public abstract class AbstractDataTypeCache {

    public AbstractDataTypeCache() {
    }

    protected static Set<String> allDataTypes = new HashSet<>();

    public void extractFromItem(View view) {
        Object definition = view.getDefinition();
        if (definition instanceof DataObjectReference) {
            DataObjectReference dataObject = (DataObjectReference) definition;
            allDataTypes.add(dataObject.getType().getValue().getType());
        } else if (definition instanceof AdHocSubprocess) {
            AdHocSubprocess adhoc = (AdHocSubprocess) definition;
            allDataTypes.addAll(getDataTypes(adhoc.getProcessData().getProcessVariables()));
        } else if (definition instanceof Process) {
            Process diagram = (Process) definition;
            allDataTypes.addAll(getDataTypes(diagram.getProcessData().getProcessVariables()));
        } else if (definition instanceof EmbeddedSubprocess) {
            EmbeddedSubprocess embeddedSubprocess = (EmbeddedSubprocess) definition;
            allDataTypes.addAll(getDataTypes(embeddedSubprocess.getProcessData().getProcessVariables()));
        } else if (definition instanceof EventSubprocess) {
            EventSubprocess eventSubprocess = (EventSubprocess) definition;
            allDataTypes.addAll(getDataTypes(eventSubprocess.getProcessData().getProcessVariables()));
        } else if (definition instanceof MultipleInstanceSubprocess) {
            MultipleInstanceSubprocess multipleInstanceSubprocess = (MultipleInstanceSubprocess) definition;
            allDataTypes.addAll(getDataTypes(multipleInstanceSubprocess.getProcessData().getProcessVariables()));
            allDataTypes.addAll(getDataTypes(multipleInstanceSubprocess.getExecutionSet().getMultipleInstanceDataInput().getValue()));
            allDataTypes.addAll(getDataTypes(multipleInstanceSubprocess.getExecutionSet().getMultipleInstanceDataOutput().getValue()));
        } else if (definition instanceof UserTask) {
            UserTask userTask = (UserTask) definition;
            allDataTypes.addAll(processAssignments(userTask.getExecutionSet().getAssignmentsinfo()));
        } else if (definition instanceof GenericServiceTask) {
            GenericServiceTask genericServiceTask = (GenericServiceTask) definition;
            allDataTypes.addAll(processAssignments(genericServiceTask.getExecutionSet().getAssignmentsinfo()));
        } else if (definition instanceof BusinessRuleTask) {
            BusinessRuleTask businessRuleTask = (BusinessRuleTask) definition;
            allDataTypes.addAll(processAssignments(businessRuleTask.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndErrorEvent) {
            EndErrorEvent endErrorEvent = (EndErrorEvent) definition;
            allDataTypes.addAll(processAssignments(endErrorEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndEscalationEvent) {
            EndEscalationEvent endEscalationEvent = (EndEscalationEvent) definition;
            allDataTypes.addAll(processAssignments(endEscalationEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndMessageEvent) {
            EndMessageEvent endMessageEvent = (EndMessageEvent) definition;
            allDataTypes.addAll(processAssignments(endMessageEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndSignalEvent) {
            EndSignalEvent endSignalEvent = (EndSignalEvent) definition;
            allDataTypes.addAll(processAssignments(endSignalEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateLinkEventCatching) {
            IntermediateLinkEventCatching intermediateLinkEventCatching = (IntermediateLinkEventCatching) definition;
            allDataTypes.addAll(processAssignments(intermediateLinkEventCatching.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateLinkEventThrowing) {
            IntermediateLinkEventThrowing intermediateLinkEventThrowing = (IntermediateLinkEventThrowing) definition;
            allDataTypes.addAll(processAssignments(intermediateLinkEventThrowing.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateErrorEventCatching) {
            IntermediateErrorEventCatching intermediateErrorEventCatching = (IntermediateErrorEventCatching) definition;
            allDataTypes.addAll(processAssignments(intermediateErrorEventCatching.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateEscalationEvent) {
            IntermediateEscalationEvent intermediateEscalationEvent = (IntermediateEscalationEvent) definition;
            allDataTypes.addAll(processAssignments(intermediateEscalationEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateEscalationEventThrowing) {
            IntermediateEscalationEventThrowing intermediateEscalationEventThrowing = (IntermediateEscalationEventThrowing) definition;
            allDataTypes.addAll(processAssignments(intermediateEscalationEventThrowing.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateMessageEventCatching) {
            IntermediateMessageEventCatching intermediateMessageEventCatching = (IntermediateMessageEventCatching) definition;
            allDataTypes.addAll(processAssignments(intermediateMessageEventCatching.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateMessageEventThrowing) {
            IntermediateMessageEventThrowing intermediateMessageEventThrowing = (IntermediateMessageEventThrowing) definition;
            allDataTypes.addAll(processAssignments(intermediateMessageEventThrowing.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateSignalEventCatching) {
            IntermediateSignalEventCatching intermediateSignalEventCatching = (IntermediateSignalEventCatching) definition;
            allDataTypes.addAll(processAssignments(intermediateSignalEventCatching.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateSignalEventThrowing) {
            IntermediateSignalEventThrowing intermediateSignalEventThrowing = (IntermediateSignalEventThrowing) definition;
            allDataTypes.addAll(processAssignments(intermediateSignalEventThrowing.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof ReusableSubprocess) {
            ReusableSubprocess reusableSubprocess = (ReusableSubprocess) definition;
            allDataTypes.addAll(processAssignments(reusableSubprocess.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartErrorEvent) {
            StartErrorEvent startErrorEvent = (StartErrorEvent) definition;
            allDataTypes.addAll(processAssignments(startErrorEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartEscalationEvent) {
            StartEscalationEvent startEscalationEvent = (StartEscalationEvent) definition;
            allDataTypes.addAll(processAssignments(startEscalationEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartMessageEvent) {
            StartMessageEvent startMessageEvent = (StartMessageEvent) definition;
            allDataTypes.addAll(processAssignments(startMessageEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartSignalEvent) {
            StartSignalEvent startSignalEvent = (StartSignalEvent) definition;
            allDataTypes.addAll(processAssignments(startSignalEvent.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof CustomTask) {
            CustomTask customTask = (CustomTask) definition;
            allDataTypes.addAll(processAssignments(customTask.getDataIOSet().getAssignmentsinfo()));
        }
    }

    protected abstract void cacheDataTypes(Object processRoot);

    protected abstract List<String> processAssignments(AssignmentsInfo info);

    protected abstract List<String> getDataTypes(String variables);

    private void cacheImports(List<DefaultImport> defaultImports) {
        for (DefaultImport imported : defaultImports) {
            allDataTypes.add(imported.getClassName() == null ? "Object" : imported.getClassName());
        }
    }

    public void initCache(Object diagramRoot, Node<View<? extends BPMNDiagram<? extends BaseDiagramSet, ? extends BaseProcessData, ? extends BaseRootProcessAdvancedData>>, Edge> value) {
        allDataTypes.clear();
        final BPMNDiagram<? extends BaseDiagramSet, ? extends BaseProcessData, ? extends BaseRootProcessAdvancedData> definition = value.getContent().getDefinition();
        cacheImports(definition.getImports().getValue().getDefaultImports());
        cacheProcessVariables(definition.getProcessData().getProcessVariables());
        cacheGlobalVariables(definition.getAdvancedData().getGlobalVariables().getValue());
        cacheDataTypes(diagramRoot);
    }

    private void cacheProcessVariables(String processVariables) {
        allDataTypes.addAll(getDataTypes(processVariables));
    }

    private void cacheGlobalVariables(String globalVariables) {
        allDataTypes.addAll(getDataTypes(globalVariables));
    }

    public Set<String> getCachedDataTypes() {
        allDataTypes.remove("Object");
        allDataTypes.remove("String");
        allDataTypes.remove("Integer");
        allDataTypes.remove("Boolean");
        allDataTypes.remove("Float");
        return allDataTypes;
    }
}

