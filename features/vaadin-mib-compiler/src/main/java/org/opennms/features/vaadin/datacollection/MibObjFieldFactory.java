/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.features.vaadin.datacollection;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

/**
 * The MIB Object Field.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a> 
 */
@SuppressWarnings("serial")
public class MibObjFieldFactory implements TableFieldFactory {

    /** The resource types. */
    private final List<String> resourceTypes;

    /**
     * Instantiates a new MIB Object field factory.
     *
     * @param resourceTypes the available resource types
     */
    public MibObjFieldFactory(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    @Override
    public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
        if (propertyId.equals("oid")) {
            final TextField field = new TextField();
            field.setRequired(true);
            field.addValidator(new RegexpValidator("[.\\d]+", "Invalid OID {0}"));
            return field;
        }
        if (propertyId.equals("instance")) {
            final ComboBox field = new ComboBox();
            field.setImmediate(true);
            field.setNullSelectionAllowed(false);
            field.setNewItemsAllowed(true);
            field.setNewItemHandler(new NewItemHandler() {
                public void addNewItem(String newItemCaption) {
                    if (!field.containsId(newItemCaption)) {
                        field.addItem(newItemCaption);
                        field.setValue(newItemCaption);
                    }
                }
            });
            field.addItem("0");
            field.addItem("ifIndex");
            for (String rt : resourceTypes) {
                field.addItem(rt);
            }
            return field;
        }
        if (propertyId.equals("alias")) {
            final TextField field = new TextField();
            field.setRequired(true);
            return field;
        }
        if (propertyId.equals("type")) {
            final TextField field = new TextField();
            field.setRequired(true);
            field.addValidator(new RegexpValidator("^(gauge|counter|integer|string|octetstring)",
                    "Invalid type {0}. Valid types are: gauge, integer, counter, string"));
            return field;
        }
        return null;
    }

}