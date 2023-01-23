package com.mojang.math;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Matrix4x3fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Transformation implements net.minecraftforge.client.extensions.IForgeTransformation {
   private final Matrix4f matrix;
   private boolean decomposed;
   @Nullable
   private Vector3f translation;
   @Nullable
   private Quaternionf leftRotation;
   @Nullable
   private Vector3f scale;
   @Nullable
   private Quaternionf rightRotation;
   private static final Transformation IDENTITY = Util.make(() -> {
      Transformation transformation = new Transformation(new Matrix4f());
      transformation.getLeftRotation();
      return transformation;
   });

   public Transformation(@Nullable Matrix4f pMatrix) {
      if (pMatrix == null) {
         this.matrix = IDENTITY.matrix;
      } else {
         this.matrix = pMatrix;
      }

   }

   public Transformation(@Nullable Vector3f pTranslation, @Nullable Quaternionf pLeftRotation, @Nullable Vector3f pScale, @Nullable Quaternionf pRightRotation) {
      this.matrix = compose(pTranslation, pLeftRotation, pScale, pRightRotation);
      this.translation = pTranslation != null ? pTranslation : new Vector3f();
      this.leftRotation = pLeftRotation != null ? pLeftRotation : new Quaternionf();
      this.scale = pScale != null ? pScale : new Vector3f(1.0F, 1.0F, 1.0F);
      this.rightRotation = pRightRotation != null ? pRightRotation : new Quaternionf();
      this.decomposed = true;
   }

   public static Transformation identity() {
      return IDENTITY;
   }

   public Transformation compose(Transformation pOther) {
      Matrix4f matrix4f = this.getMatrix();
      matrix4f.mul(pOther.getMatrix());
      return new Transformation(matrix4f);
   }

   @Nullable
   public Transformation inverse() {
      if (this == IDENTITY) {
         return this;
      } else {
         Matrix4f matrix4f = this.getMatrix().invert();
         return matrix4f.isFinite() ? new Transformation(matrix4f) : null;
      }
   }

   private void ensureDecomposed() {
      if (!this.decomposed) {
         Matrix4x3f matrix4x3f = MatrixUtil.toAffine(this.matrix);
         Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose((new Matrix3f()).set((Matrix4x3fc)matrix4x3f));
         this.translation = matrix4x3f.getTranslation(new Vector3f());
         this.leftRotation = new Quaternionf(triple.getLeft());
         this.scale = new Vector3f(triple.getMiddle());
         this.rightRotation = new Quaternionf(triple.getRight());
         this.decomposed = true;
      }

   }

   private static Matrix4f compose(@Nullable Vector3f pTranslation, @Nullable Quaternionf pLeftRotation, @Nullable Vector3f pScale, @Nullable Quaternionf pRightRotation) {
      Matrix4f matrix4f = new Matrix4f();
      if (pTranslation != null) {
         matrix4f.translation(pTranslation);
      }

      if (pLeftRotation != null) {
         matrix4f.rotate(pLeftRotation);
      }

      if (pScale != null) {
         matrix4f.scale(pScale);
      }

      if (pRightRotation != null) {
         matrix4f.rotate(pRightRotation);
      }

      return matrix4f;
   }

   public Matrix4f getMatrix() {
      return new Matrix4f(this.matrix);
   }

   public Vector3f getTranslation() {
      this.ensureDecomposed();
      return new Vector3f((Vector3fc)this.translation);
   }

   public Quaternionf getLeftRotation() {
      this.ensureDecomposed();
      return new Quaternionf(this.leftRotation);
   }

   public Vector3f getScale() {
      this.ensureDecomposed();
      return new Vector3f((Vector3fc)this.scale);
   }

   public Quaternionf getRightRotation() {
      this.ensureDecomposed();
      return new Quaternionf(this.rightRotation);
   }

   public boolean equals(Object pOther) {
      if (this == pOther) {
         return true;
      } else if (pOther != null && this.getClass() == pOther.getClass()) {
         Transformation transformation = (Transformation)pOther;
         return Objects.equals(this.matrix, transformation.matrix);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.matrix);
   }

    private Matrix3f normalTransform = null;
    public Matrix3f getNormalMatrix() {
        checkNormalTransform();
        return normalTransform;
    }
    private void checkNormalTransform() {
        if (normalTransform == null) {
            normalTransform = new Matrix3f(matrix);
            normalTransform.invert();
            normalTransform.transpose();
        }
    }

   public Transformation slerp(Transformation pTransformation, float pDelta) {
      Vector3f vector3f = this.getTranslation();
      Quaternionf quaternionf = this.getLeftRotation();
      Vector3f vector3f1 = this.getScale();
      Quaternionf quaternionf1 = this.getRightRotation();
      vector3f.lerp(pTransformation.getTranslation(), pDelta);
      quaternionf.slerp(pTransformation.getLeftRotation(), pDelta);
      vector3f1.lerp(pTransformation.getScale(), pDelta);
      quaternionf1.slerp(pTransformation.getRightRotation(), pDelta);
      return new Transformation(vector3f, quaternionf, vector3f1, quaternionf1);
   }
}
