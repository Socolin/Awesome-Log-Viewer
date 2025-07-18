package io.opentelemetry.proto.collector.profiles.v1development;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service that can be used to push profiles between one Application instrumented with
 * OpenTelemetry and a collector, or between a collector and a central collector.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: opentelemetry/proto/collector/profiles/v1development/profiles_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ProfilesServiceGrpc {

  private ProfilesServiceGrpc() {}

  public static final String SERVICE_NAME = "opentelemetry.proto.collector.profiles.v1development.ProfilesService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest,
      io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse> getExportMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Export",
      requestType = io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest.class,
      responseType = io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest,
      io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse> getExportMethod() {
    io.grpc.MethodDescriptor<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest, io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse> getExportMethod;
    if ((getExportMethod = ProfilesServiceGrpc.getExportMethod) == null) {
      synchronized (ProfilesServiceGrpc.class) {
        if ((getExportMethod = ProfilesServiceGrpc.getExportMethod) == null) {
          ProfilesServiceGrpc.getExportMethod = getExportMethod =
              io.grpc.MethodDescriptor.<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest, io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Export"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProfilesServiceMethodDescriptorSupplier("Export"))
              .build();
        }
      }
    }
    return getExportMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ProfilesServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProfilesServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProfilesServiceStub>() {
        @java.lang.Override
        public ProfilesServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProfilesServiceStub(channel, callOptions);
        }
      };
    return ProfilesServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ProfilesServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProfilesServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProfilesServiceBlockingStub>() {
        @java.lang.Override
        public ProfilesServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProfilesServiceBlockingStub(channel, callOptions);
        }
      };
    return ProfilesServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ProfilesServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProfilesServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProfilesServiceFutureStub>() {
        @java.lang.Override
        public ProfilesServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProfilesServiceFutureStub(channel, callOptions);
        }
      };
    return ProfilesServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service that can be used to push profiles between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector.
   * </pre>
   */
  public static abstract class ProfilesServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void export(io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest request,
        io.grpc.stub.StreamObserver<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExportMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getExportMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest,
                io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse>(
                  this, METHODID_EXPORT)))
          .build();
    }
  }

  /**
   * <pre>
   * Service that can be used to push profiles between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector.
   * </pre>
   */
  public static final class ProfilesServiceStub extends io.grpc.stub.AbstractAsyncStub<ProfilesServiceStub> {
    private ProfilesServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProfilesServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProfilesServiceStub(channel, callOptions);
    }

    /**
     */
    public void export(io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest request,
        io.grpc.stub.StreamObserver<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExportMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Service that can be used to push profiles between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector.
   * </pre>
   */
  public static final class ProfilesServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ProfilesServiceBlockingStub> {
    private ProfilesServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProfilesServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProfilesServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse export(io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExportMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Service that can be used to push profiles between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector.
   * </pre>
   */
  public static final class ProfilesServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ProfilesServiceFutureStub> {
    private ProfilesServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProfilesServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProfilesServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse> export(
        io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExportMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXPORT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ProfilesServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ProfilesServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXPORT:
          serviceImpl.export((io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceRequest) request,
              (io.grpc.stub.StreamObserver<io.opentelemetry.proto.collector.profiles.v1development.ExportProfilesServiceResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ProfilesServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ProfilesServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.opentelemetry.proto.collector.profiles.v1development.ProfilesServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ProfilesService");
    }
  }

  private static final class ProfilesServiceFileDescriptorSupplier
      extends ProfilesServiceBaseDescriptorSupplier {
    ProfilesServiceFileDescriptorSupplier() {}
  }

  private static final class ProfilesServiceMethodDescriptorSupplier
      extends ProfilesServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ProfilesServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ProfilesServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ProfilesServiceFileDescriptorSupplier())
              .addMethod(getExportMethod())
              .build();
        }
      }
    }
    return result;
  }
}
