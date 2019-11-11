/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.commands;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationContextTest extends AbstractScenarioSimulationTest {
    

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void getScenarioGridPanel() {
        assertEquals(scenarioGridPanelMock, scenarioSimulationContextLocal.getSelectedScenarioGridPanel());
    }

    @Test
    public void getModel() {
        assertEquals(scenarioGridModelMock, scenarioSimulationContextLocal.getSelectedScenarioGridModel());
    }

    @Test
    public void getScenarioGridLayer() {
        assertEquals(scenarioGridLayerMock, scenarioSimulationContextLocal.getSelectedScenarioGridLayer());
    }

    @Test
    public void getCollectionEditorSingletonDOMElementFactory() {
        assertEquals(collectionEditorSingletonDOMElementFactoryTest,
                     scenarioSimulationContextLocal.getCollectionEditorSingletonDOMElementFactory());
    }

    @Test
    public void getScenarioCellTextAreaSingletonDOMElementFactory() {
        assertEquals(scenarioCellTextAreaSingletonDOMElementFactoryTest,
                     scenarioSimulationContextLocal.getScenarioCellTextAreaSingletonDOMElementFactory());
    }

    @Test
    public void getScenarioHeaderTextBoxSingletonDOMElementFactory() {
        assertEquals(scenarioHeaderTextBoxSingletonDOMElementFactoryTest,
                     scenarioSimulationContextLocal.getScenarioHeaderTextBoxSingletonDOMElementFactory());
    }

    @Test
    public void getScenarioExpressionCellTextAreaSingletonDOMElementFactory() {
        assertEquals(scenarioExpressionCellTextAreaSingletonDOMElementFactoryMock,
                     scenarioSimulationContextLocal.getScenarioExpressionCellTextAreaSingletonDOMElementFactory());
    }
}