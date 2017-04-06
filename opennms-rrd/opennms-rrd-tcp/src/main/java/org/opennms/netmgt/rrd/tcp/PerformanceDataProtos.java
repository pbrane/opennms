/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

// Generated by the protocol buffer compiler.  DO NOT EDIT!

package org.opennms.netmgt.rrd.tcp;

public final class PerformanceDataProtos {
  private PerformanceDataProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class PerformanceDataReading extends
      com.google.protobuf.GeneratedMessage {
    // Use PerformanceDataReading.newBuilder() to construct.
    private PerformanceDataReading() {}
    
    private static final PerformanceDataReading defaultInstance = new PerformanceDataReading();
    public static PerformanceDataReading getDefaultInstance() {
      return defaultInstance;
    }
    
    public PerformanceDataReading getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.internal_static_PerformanceDataReading_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.internal_static_PerformanceDataReading_fieldAccessorTable;
    }
    
    // required string path = 1;
    public static final int PATH_FIELD_NUMBER = 1;
    private boolean hasPath;
    private java.lang.String path_ = "";
    public boolean hasPath() { return hasPath; }
    public java.lang.String getPath() { return path_; }
    
    // required string owner = 2;
    public static final int OWNER_FIELD_NUMBER = 2;
    private boolean hasOwner;
    private java.lang.String owner_ = "";
    public boolean hasOwner() { return hasOwner; }
    public java.lang.String getOwner() { return owner_; }
    
    // required uint64 timestamp = 3;
    public static final int TIMESTAMP_FIELD_NUMBER = 3;
    private boolean hasTimestamp;
    private long timestamp_ = 0L;
    public boolean hasTimestamp() { return hasTimestamp; }
    public long getTimestamp() { return timestamp_; }
    
    // repeated double dblValue = 4;
    public static final int DBLVALUE_FIELD_NUMBER = 4;
    private java.util.List<java.lang.Double> dblValue_ =
      java.util.Collections.emptyList();
    public java.util.List<java.lang.Double> getDblValueList() {
      return dblValue_;
    }
    public int getDblValueCount() { return dblValue_.size(); }
    public double getDblValue(int index) {
      return dblValue_.get(index);
    }
    
    // repeated string strValue = 5;
    public static final int STRVALUE_FIELD_NUMBER = 5;
    private java.util.List<java.lang.String> strValue_ =
      java.util.Collections.emptyList();
    public java.util.List<java.lang.String> getStrValueList() {
      return strValue_;
    }
    public int getStrValueCount() { return strValue_.size(); }
    public java.lang.String getStrValue(int index) {
      return strValue_.get(index);
    }
    
    public final boolean isInitialized() {
      if (!hasPath) return false;
      if (!hasOwner) return false;
      if (!hasTimestamp) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (hasPath()) {
        output.writeString(1, getPath());
      }
      if (hasOwner()) {
        output.writeString(2, getOwner());
      }
      if (hasTimestamp()) {
        output.writeUInt64(3, getTimestamp());
      }
      for (double element : getDblValueList()) {
        output.writeDouble(4, element);
      }
      for (java.lang.String element : getStrValueList()) {
        output.writeString(5, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasPath()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getPath());
      }
      if (hasOwner()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getOwner());
      }
      if (hasTimestamp()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(3, getTimestamp());
      }
      {
        int dataSize = 0;
        dataSize = 8 * getDblValueList().size();
        size += dataSize;
        size += 1 * getDblValueList().size();
      }
      {
        int dataSize = 0;
        for (java.lang.String element : getStrValueList()) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeStringSizeNoTag(element);
        }
        size += dataSize;
        size += 1 * getStrValueList().size();
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading result;
      
      // Construct using org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading();
        return builder;
      }
      
      protected org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.getDescriptor();
      }
      
      public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading getDefaultInstanceForType() {
        return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.dblValue_ != java.util.Collections.EMPTY_LIST) {
          result.dblValue_ =
            java.util.Collections.unmodifiableList(result.dblValue_);
        }
        if (result.strValue_ != java.util.Collections.EMPTY_LIST) {
          result.strValue_ =
            java.util.Collections.unmodifiableList(result.strValue_);
        }
        org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading) {
          return mergeFrom((org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading other) {
        if (other == org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.getDefaultInstance()) return this;
        if (other.hasPath()) {
          setPath(other.getPath());
        }
        if (other.hasOwner()) {
          setOwner(other.getOwner());
        }
        if (other.hasTimestamp()) {
          setTimestamp(other.getTimestamp());
        }
        if (!other.dblValue_.isEmpty()) {
          if (result.dblValue_.isEmpty()) {
            result.dblValue_ = new java.util.ArrayList<java.lang.Double>();
          }
          result.dblValue_.addAll(other.dblValue_);
        }
        if (!other.strValue_.isEmpty()) {
          if (result.strValue_.isEmpty()) {
            result.strValue_ = new java.util.ArrayList<java.lang.String>();
          }
          result.strValue_.addAll(other.strValue_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              setPath(input.readString());
              break;
            }
            case 18: {
              setOwner(input.readString());
              break;
            }
            case 24: {
              setTimestamp(input.readUInt64());
              break;
            }
            case 33: {
              addDblValue(input.readDouble());
              break;
            }
            case 42: {
              addStrValue(input.readString());
              break;
            }
          }
        }
      }
      
      
      // required string path = 1;
      public boolean hasPath() {
        return result.hasPath();
      }
      public java.lang.String getPath() {
        return result.getPath();
      }
      public Builder setPath(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasPath = true;
        result.path_ = value;
        return this;
      }
      public Builder clearPath() {
        result.hasPath = false;
        result.path_ = getDefaultInstance().getPath();
        return this;
      }
      
      // required string owner = 2;
      public boolean hasOwner() {
        return result.hasOwner();
      }
      public java.lang.String getOwner() {
        return result.getOwner();
      }
      public Builder setOwner(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasOwner = true;
        result.owner_ = value;
        return this;
      }
      public Builder clearOwner() {
        result.hasOwner = false;
        result.owner_ = getDefaultInstance().getOwner();
        return this;
      }
      
      // required uint64 timestamp = 3;
      public boolean hasTimestamp() {
        return result.hasTimestamp();
      }
      public long getTimestamp() {
        return result.getTimestamp();
      }
      public Builder setTimestamp(long value) {
        result.hasTimestamp = true;
        result.timestamp_ = value;
        return this;
      }
      public Builder clearTimestamp() {
        result.hasTimestamp = false;
        result.timestamp_ = 0L;
        return this;
      }
      
      // repeated double dblValue = 4;
      public java.util.List<java.lang.Double> getDblValueList() {
        return java.util.Collections.unmodifiableList(result.dblValue_);
      }
      public int getDblValueCount() {
        return result.getDblValueCount();
      }
      public double getDblValue(int index) {
        return result.getDblValue(index);
      }
      public Builder setDblValue(int index, double value) {
        result.dblValue_.set(index, value);
        return this;
      }
      public Builder addDblValue(double value) {
        if (result.dblValue_.isEmpty()) {
          result.dblValue_ = new java.util.ArrayList<java.lang.Double>();
        }
        result.dblValue_.add(value);
        return this;
      }
      public Builder addAllDblValue(
          java.lang.Iterable<? extends java.lang.Double> values) {
        if (result.dblValue_.isEmpty()) {
          result.dblValue_ = new java.util.ArrayList<java.lang.Double>();
        }
        super.addAll(values, result.dblValue_);
        return this;
      }
      public Builder clearDblValue() {
        result.dblValue_ = java.util.Collections.emptyList();
        return this;
      }
      
      // repeated string strValue = 5;
      public java.util.List<java.lang.String> getStrValueList() {
        return java.util.Collections.unmodifiableList(result.strValue_);
      }
      public int getStrValueCount() {
        return result.getStrValueCount();
      }
      public java.lang.String getStrValue(int index) {
        return result.getStrValue(index);
      }
      public Builder setStrValue(int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.strValue_.set(index, value);
        return this;
      }
      public Builder addStrValue(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  if (result.strValue_.isEmpty()) {
          result.strValue_ = new java.util.ArrayList<java.lang.String>();
        }
        result.strValue_.add(value);
        return this;
      }
      public Builder addAllStrValue(
          java.lang.Iterable<? extends java.lang.String> values) {
        if (result.strValue_.isEmpty()) {
          result.strValue_ = new java.util.ArrayList<java.lang.String>();
        }
        super.addAll(values, result.strValue_);
        return this;
      }
      public Builder clearStrValue() {
        result.strValue_ = java.util.Collections.emptyList();
        return this;
      }
    }
    
    static {
      org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.getDescriptor();
    }
    
    static {
      org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.internalForceInit();
    }
  }
  
  public static final class PerformanceDataReadings extends
      com.google.protobuf.GeneratedMessage {
    // Use PerformanceDataReadings.newBuilder() to construct.
    private PerformanceDataReadings() {}
    
    private static final PerformanceDataReadings defaultInstance = new PerformanceDataReadings();
    public static PerformanceDataReadings getDefaultInstance() {
      return defaultInstance;
    }
    
    public PerformanceDataReadings getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.internal_static_PerformanceDataReadings_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.internal_static_PerformanceDataReadings_fieldAccessorTable;
    }
    
    // repeated .PerformanceDataReading message = 1;
    public static final int MESSAGE_FIELD_NUMBER = 1;
    private java.util.List<org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading> message_ =
      java.util.Collections.emptyList();
    public java.util.List<org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading> getMessageList() {
      return message_;
    }
    public int getMessageCount() { return message_.size(); }
    public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading getMessage(int index) {
      return message_.get(index);
    }
    
    public final boolean isInitialized() {
      for (org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading element : getMessageList()) {
        if (!element.isInitialized()) return false;
      }
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      for (org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading element : getMessageList()) {
        output.writeMessage(1, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      for (org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading element : getMessageList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeDelimitedFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings result;
      
      // Construct using org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings();
        return builder;
      }
      
      protected org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings.getDescriptor();
      }
      
      public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings getDefaultInstanceForType() {
        return org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.message_ != java.util.Collections.EMPTY_LIST) {
          result.message_ =
            java.util.Collections.unmodifiableList(result.message_);
        }
        org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings) {
          return mergeFrom((org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings other) {
        if (other == org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings.getDefaultInstance()) return this;
        if (!other.message_.isEmpty()) {
          if (result.message_.isEmpty()) {
            result.message_ = new java.util.ArrayList<org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading>();
          }
          result.message_.addAll(other.message_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.Builder subBuilder = org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addMessage(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // repeated .PerformanceDataReading message = 1;
      public java.util.List<org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading> getMessageList() {
        return java.util.Collections.unmodifiableList(result.message_);
      }
      public int getMessageCount() {
        return result.getMessageCount();
      }
      public org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading getMessage(int index) {
        return result.getMessage(index);
      }
      public Builder setMessage(int index, org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.message_.set(index, value);
        return this;
      }
      public Builder setMessage(int index, org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.Builder builderForValue) {
        result.message_.set(index, builderForValue.build());
        return this;
      }
      public Builder addMessage(org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.message_.isEmpty()) {
          result.message_ = new java.util.ArrayList<org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading>();
        }
        result.message_.add(value);
        return this;
      }
      public Builder addMessage(org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.Builder builderForValue) {
        if (result.message_.isEmpty()) {
          result.message_ = new java.util.ArrayList<org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading>();
        }
        result.message_.add(builderForValue.build());
        return this;
      }
      public Builder addAllMessage(
          java.lang.Iterable<? extends org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading> values) {
        if (result.message_.isEmpty()) {
          result.message_ = new java.util.ArrayList<org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading>();
        }
        super.addAll(values, result.message_);
        return this;
      }
      public Builder clearMessage() {
        result.message_ = java.util.Collections.emptyList();
        return this;
      }
    }
    
    static {
      org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.getDescriptor();
    }
    
    static {
      org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.internalForceInit();
    }
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_PerformanceDataReading_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_PerformanceDataReading_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_PerformanceDataReadings_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_PerformanceDataReadings_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\025PerformanceData.proto\"l\n\026PerformanceDa" +
      "taReading\022\014\n\004path\030\001 \002(\t\022\r\n\005owner\030\002 \002(\t\022\021" +
      "\n\ttimestamp\030\003 \002(\004\022\020\n\010dblValue\030\004 \003(\001\022\020\n\010s" +
      "trValue\030\005 \003(\t\"C\n\027PerformanceDataReadings" +
      "\022(\n\007message\030\001 \003(\0132\027.PerformanceDataReadi" +
      "ngB3\n\032org.opennms.netmgt.rrd.tcpB\025Perfor" +
      "manceDataProtos"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_PerformanceDataReading_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_PerformanceDataReading_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_PerformanceDataReading_descriptor,
              new java.lang.String[] { "Path", "Owner", "Timestamp", "DblValue", "StrValue", },
              org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.class,
              org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReading.Builder.class);
          internal_static_PerformanceDataReadings_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_PerformanceDataReadings_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_PerformanceDataReadings_descriptor,
              new java.lang.String[] { "Message", },
              org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings.class,
              org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
}