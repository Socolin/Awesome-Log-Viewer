// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opentelemetry/proto/profiles/v1development/profiles.proto

package io.opentelemetry.proto.profiles.v1development;

public interface LineOrBuilder extends
    // @@protoc_insertion_point(interface_extends:opentelemetry.proto.profiles.v1development.Line)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Reference to function in Profile.function_table.
   * </pre>
   *
   * <code>int32 function_index = 1;</code>
   * @return The functionIndex.
   */
  int getFunctionIndex();

  /**
   * <pre>
   * Line number in source code. 0 means unset.
   * </pre>
   *
   * <code>int64 line = 2;</code>
   * @return The line.
   */
  long getLine();

  /**
   * <pre>
   * Column number in source code. 0 means unset.
   * </pre>
   *
   * <code>int64 column = 3;</code>
   * @return The column.
   */
  long getColumn();
}
