/*
 **********************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *               2018 Red Hat, Inc. and/or its affiliates
 *               and other contributors as indicated by the @author tags.
 *
 * See the NOTICES file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 **********************************************************************/
package org.eclipse.microprofile.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The {@link Metadata} builder.
 * This builder has a default value:
 * {@link MetadataBuilder#type} as {@link MetricType#INVALID}
 * {@link MetadataBuilder#unit} as {@link MetricUnits#NONE}
 * {@link MetadataBuilder#reusable} as {@link Boolean#FALSE}
 */
public class MetadataBuilder {

    public static final String GLOBAL_TAGS_VARIABLE = "MP_METRICS_TAGS";

    private String name;

    private String displayName;

    private String description;

    private MetricType type = MetricType.INVALID;

    private String unit = MetricUnits.NONE;

    private boolean reusable = false;

    private Map<String, String> tags = new HashMap<>();

    MetadataBuilder() {
        String globalTagsFromEnv = System.getenv(GLOBAL_TAGS_VARIABLE);
        addTags(globalTagsFromEnv);
    }

    MetadataBuilder(Metadata metadata) {
        this.name = metadata.getName();
        this.type = metadata.getTypeRaw();
        this.reusable = metadata.isReusable();
        this.tags.putAll(metadata.getTags());
        this.displayName = metadata.getDisplayName();
        metadata.getDescription().ifPresent(this::withDescription);
        metadata.getUnit().ifPresent(this::withUnit);

    }


    /**
     * Sets the name
     *
     * @param name the name
     * @return the builder instance
     * @throws NullPointerException when name is null
     */
    public MetadataBuilder withName(String name) {
        this.name = Objects.requireNonNull(name, "name is required");
        return this;
    }

    /**
     * Sets the displayName
     *
     * @param displayName the displayName
     * @return the builder instance
     * @throws NullPointerException when displayName is null
     */
    public MetadataBuilder withDisplayName(String displayName) {
        this.displayName = Objects.requireNonNull(displayName, "displayName is required");
        return this;
    }

    /**
     * Sets the description
     *
     * @param description the name
     * @return the builder instance
     * @throws NullPointerException when description is null
     */
    public MetadataBuilder withDescription(String description) {
        this.description = Objects.requireNonNull(description, "description is required");
        return this;
    }

    /**
     * Sets the type
     *
     * @param type the name
     * @return the builder instance
     * @throws NullPointerException when type is null
     */
    public MetadataBuilder withType(MetricType type) {
        this.type = Objects.requireNonNull(type, "type is required");
        return this;
    }

    /**
     * Sets the unit
     *
     * @param unit the name
     * @return the builder instance
     * @throws NullPointerException when unit is null
     */
    public MetadataBuilder withUnit(String unit) {
        this.unit = Objects.requireNonNull(unit, "unit is required");
        return this;
    }

    /**
     * Sets the reusable to {@link Boolean#TRUE}
     *
     * @return the builder instance
     */
    public MetadataBuilder reusable() {
        this.reusable = true;
        return this;
    }

    /**
     * Sets the reusable to {@link Boolean#FALSE}
     *
     * @return the builder instance
     */
    public MetadataBuilder notReusable() {
        this.reusable = false;
        return this;
    }

    /**
     * Add multiple tags delimited by commas.
     * The format must be in the form 'key1=value1, key2=value2'.
     * This method will call {@link #addTag(String)} on each tag.
     *
     * @param tagsString a string containing multiple tags
     * @return this instance with new tags
     */
    public MetadataBuilder addTags(String tagsString) {
        if (tagsString == null || tagsString.isEmpty()) {
            return this;
        }
        String[] singleTags = tagsString.split(",");
        Stream.of(singleTags).map(String::trim).forEach(this::addTag);
        return this;
    }

    /**
     * Add one single tag with the format: 'key=value'. If the input is empty or does
     * not contain a '=' sign, the entry is ignored.
     *
     * @param kvString Input string
     * @return this instance with new tag
     */
    public MetadataBuilder addTag(String kvString) {
        if (kvString == null || kvString.isEmpty() || !kvString.contains("=")) {
            return this;
        }
        tags.put(kvString.substring(0, kvString.indexOf("=")), kvString.substring(kvString.indexOf("=") + 1));
        return this;
    }

    /**
     * @return
     * @throws IllegalStateException when either name is null
     */
    public Metadata build() {
        if (Objects.isNull(name)) {
            throw new IllegalStateException("Name is required");
        }

        return new DefaultMetadata(name, displayName, description, type, unit, reusable, tags);
    }
}
