/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CustomEvent;
import elemental2.dom.Event;

public class LienzoPanelPrimitiveDragEventDetail extends LienzoPanelEventDetail {

    private final double dragX;

    private final double dragY;

    private final IPrimitive<?> primitive;

    public static LienzoPanelPrimitiveDragEventDetail getDragDetail(Event event) {
        return (LienzoPanelPrimitiveDragEventDetail) ((CustomEvent) event).detail;
    }

    public LienzoPanelPrimitiveDragEventDetail(LienzoPanel<? extends LienzoPanel> panel,
                                               IPrimitive<?> primitive,
                                               double dragX,
                                               double dragY) {
        super(panel);
        this.primitive = primitive;
        this.dragX = dragX;
        this.dragY = dragY;
    }

    public IPrimitive getPrimitive() {
        return primitive;
    }

    public double getDragX() {
        return dragX;
    }

    public double getDragY() {
        return dragY;
    }
}
