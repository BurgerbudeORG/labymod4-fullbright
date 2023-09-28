package org.burgerbude.labymod.addons.fullbright.core.transformer;

import net.labymod.api.Constants.SystemProperties;
import net.labymod.api.addon.transform.AddonClassTransformer;
import net.labymod.api.loader.platform.PlatformEnvironment;
import net.labymod.api.mapping.MappingService;
import net.labymod.api.mapping.provider.MappingProvider;
import net.labymod.api.mapping.provider.child.ClassMapping;
import net.labymod.api.mapping.provider.child.FieldMapping;
import net.labymod.api.mapping.provider.child.MethodMapping;
import net.labymod.api.models.addon.annotation.AddonTransformer;
import net.labymod.api.volt.asm.util.ASMHelper;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@AddonTransformer
public class LightMapTextureTransformer implements AddonClassTransformer {

  private static final boolean LEGACY_VERSION = PlatformEnvironment.isAncientOpenGL();
  private static final String TEXTURE_MODIFIER_NAME = "org/burgerbude/labymod/addons/fullbright/core/util/TextureModifier";
  private static final String TEXTURE_MODIFIER_PIXEL = "L" + TEXTURE_MODIFIER_NAME + "$Pixel;";
  private static final String MAKE_WHITE_TEXTURE_NAME = "fullbright$makeTextureWhite";
  private static final String MAKE_WHITE_TEXTURE_DESC = "()V";

  private static final String UPLOAD_LIGHT_TEXTURE_NAME = "fullbright$uploadLightTexture";
  private static final String UPLOAD_LIGHT_TEXTURE_DESC = "()V";

  private static final String UPDATE_LIGHT_STATE_NAME = "fullbright$updateLightState";
  private static final String UPDATE_LIGHT_STATE_DESC = "()V";

  private final MappingProvider provider;
  private final ClassMapping lightmapMappings;
  private final ClassMapping imageMappings;
  private final ClassMapping dynamicTextureMappings;

  public LightMapTextureTransformer() {
    this.provider = MappingService.instance().currentMappings();
    this.lightmapMappings = this.findClassMapping(LEGACY_VERSION ? "net.minecraft.client.renderer.EntityRenderer" : "net.minecraft.client.renderer.LightTexture");
    this.imageMappings = this.findClassMapping(LEGACY_VERSION ? "net.minecraft.client.renderer.EntityRenderer" : "com.mojang.blaze3d.platform.NativeImage");
    this.dynamicTextureMappings = this.findClassMapping(LEGACY_VERSION ? "net.minecraft.client.renderer.texture.DynamicTexture" : "net.minecraft.client.renderer.texture.DynamicTexture");
  }

  @Override
  public byte[] transform(final String name, final String transformedName, final byte[] classBytes) {
    String mappedName = this.lightmapMappings.getMappedName();
    mappedName = mappedName.replace('/', '.');
    if (!mappedName.equals(name)) {
      return classBytes;
    }

    byte[] newClassData = ASMHelper.transformClassData(classBytes, this::patch);
    System.setProperty(SystemProperties.ASM, "true");
    ASMHelper.writeClassData(name, newClassData);
    return newClassData;
  }

  private void patch(ClassNode node) {
    if (LEGACY_VERSION) {
      this.patchLegacyVersion(node);
    } else {
      this.patchModernVersion(node);
    }
  }

  private void patchLegacyVersion(ClassNode node) {

  }

  private void patchModernVersion(ClassNode node) {

    var updateLightTextureMethodMapping = this.lightmapMappings.getMethodMapping("updateLightTexture", "(F)V");
    for (final MethodNode method : node.methods) {
      String name = method.name;
      String mappedName = updateLightTextureMethodMapping.getMappedName();
      String mappedDescriptor = updateLightTextureMethodMapping.getMappedDescriptor();
      if (mappedName.equals(name) && mappedDescriptor.equals(method.desc)) {
        this.patchUpdateLightTextureMethod(method);
      }
    }

    this.makeTextureWhiteModern(node);
    this.updateLightStateMethodModern(node);
    this.uploadLightTextureMethodModern(node);
  }

  private void patchUpdateLightTextureMethod(MethodNode method) {

    AbstractInsnNode first = method.instructions.getFirst();
    while (first instanceof LabelNode || first instanceof LineNumberNode) {
      first = first.getNext();
    }


    InsnList newInstructions = new InsnList();

    String mappedName = this.lightmapMappings.getMappedName();

    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    newInstructions.add(
            new InvokeDynamicInsnNode(
                    "run",
                    "(L" + mappedName + ";)Ljava/lang/Runnable",
                    this.createLambdaMetafactoryHandle(),
                    Type.getType("()V"),
                    this.createVirtualHandle(mappedName, MAKE_WHITE_TEXTURE_NAME, MAKE_WHITE_TEXTURE_DESC),
                    Type.getType("()V")
            )
    );


    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    newInstructions.add(
            new InvokeDynamicInsnNode(
                    "run",
                    "(L" + mappedName + ";)Ljava/lang/Runnable",
                    this.createLambdaMetafactoryHandle(),
                    Type.getType("()V"),
                    this.createVirtualHandle(mappedName, UPLOAD_LIGHT_TEXTURE_NAME, UPLOAD_LIGHT_TEXTURE_DESC),
                    Type.getType("()V")
            )
    );


    newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    newInstructions.add(
            new InvokeDynamicInsnNode(
                    "run",
                    "(L" + mappedName + ";)Ljava/lang/Runnable",
                    this.createLambdaMetafactoryHandle(),
                    Type.getType("()V"),
                    this.createVirtualHandle(mappedName, UPDATE_LIGHT_STATE_NAME, UPDATE_LIGHT_STATE_DESC),
                    Type.getType("()V")
            )
    );


    newInstructions.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            TEXTURE_MODIFIER_NAME,
            "updateTexture",
            MethodType.methodType(boolean.class, Runnable.class, Runnable.class, Runnable.class).toMethodDescriptorString()
    ));


    LabelNode label = new LabelNode();
    newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));
    newInstructions.add(new InsnNode(Opcodes.RETURN));
    newInstructions.add(label);


    method.instructions.insertBefore(first, newInstructions);
  }

  private void makeTextureWhiteModern(ClassNode node) {
    MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE, MAKE_WHITE_TEXTURE_NAME, MAKE_WHITE_TEXTURE_DESC, null, null);

    InsnList instructions = method.instructions;

    this.insertMethodInvocation(instructions, "getWidth");
    this.insertMethodInvocation(instructions, "getHeight");

    instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    instructions.add(this.getLightPixels());
    instructions.add(new InsnNode(Opcodes.DUP));

    instructions.add(
            new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/util/Objects",
                    "requireNonNull",
                    "(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    );

    instructions.add(new InsnNode(Opcodes.POP));

    Handle bootstrapHandle = this.createLambdaMetafactoryHandle();
    Handle handle = this.createVirtualHandle(this.imageMappings.getMappedName(), this.imageMappings.getMethodMapping("setPixelRGBA", "(III)V").getMappedName(), "(III)V");
    instructions.add(
            new InvokeDynamicInsnNode(
                    "set",
                    "(L" + this.imageMappings.getMappedName() + ";)" + TEXTURE_MODIFIER_PIXEL,
                    bootstrapHandle,
                    Type.getType("(III)V"),
                    handle,
                    Type.getType("(III)V")
            )
    );
    instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TEXTURE_MODIFIER_NAME, "modifyModernTexture", "(II" + TEXTURE_MODIFIER_PIXEL + ")V"));
    instructions.add(new InsnNode(Opcodes.RETURN));


    node.methods.add(method);
  }

  private void uploadLightTextureMethodModern(ClassNode node) {

    MethodNode methodNode = new MethodNode(Opcodes.ACC_PRIVATE, UPLOAD_LIGHT_TEXTURE_NAME, UPLOAD_LIGHT_TEXTURE_DESC, null, null);

    methodNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    methodNode.instructions.add(this.getLightPixels());
    methodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, this.dynamicTextureMappings.getMappedName(), this.dynamicTextureMappings.getMethodMapping("upload", "()V").getMappedName(), "()V"));
    methodNode.instructions.add(new InsnNode(Opcodes.RETURN));

    node.methods.add(methodNode);
  }


  private void updateLightStateMethodModern(ClassNode node) {

    MethodNode methodNode = new MethodNode(Opcodes.ACC_PRIVATE, UPDATE_LIGHT_STATE_NAME, UPDATE_LIGHT_STATE_DESC, null, null);

    methodNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    methodNode.instructions.add(new InsnNode(Opcodes.ICONST_1));
    methodNode.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, this.lightmapMappings.getMappedName(), this.lightmapMappings.getFieldMapping("updateLightTexture").getMappedName(), "Z"));
    methodNode.instructions.add(new InsnNode(Opcodes.RETURN));

    node.methods.add(methodNode);
  }

  private Handle createVirtualHandle(String owner, String methodName, String desc) {
    return new Handle(Opcodes.H_INVOKEVIRTUAL, owner, methodName, desc, false);
  }

  private Handle createLambdaMetafactoryHandle() {
    return new Handle(
            Opcodes.H_INVOKESTATIC,
            Type.getInternalName(LambdaMetafactory.class),
            "metafactory",
            MethodType.methodType(
                            CallSite.class,
                            MethodHandles.Lookup.class,
                            String.class,
                            MethodType.class,
                            MethodType.class,
                            MethodType.class,
                            MethodType.class
                    )
                    .toMethodDescriptorString(),
            false
    );
  }

  private void insertMethodInvocation(InsnList instructions, String methodName) {
    instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
    instructions.add(this.getLightPixels());

    MethodMapping methodMapping = this.imageMappings.getMethodMapping(methodName, "()I");
    instructions.add(
            new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    this.imageMappings.getMappedName(),
                    methodMapping.getMappedName(),
                    methodMapping.getMappedDescriptor()
            )
    );
  }

  private FieldInsnNode getLightPixels() {
    FieldMapping fieldMapping = this.lightmapMappings.getFieldMapping("lightPixels");
    return new FieldInsnNode(
            Opcodes.GETFIELD,
            this.lightmapMappings.getMappedName(),
            fieldMapping.getMappedName(),
            fieldMapping.getMappedDescriptor()
    );
  }

  private ClassMapping findClassMapping(String name) {
    name = name.replace('.', '/');
    return this.provider.getClassMapping(name);
  }

}
