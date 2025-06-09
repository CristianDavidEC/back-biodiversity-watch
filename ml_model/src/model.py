import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import os
import numpy as np


class SpeciesClassifier:
    def __init__(self, input_shape=(224, 224, 3), num_classes=None):
        self.input_shape = input_shape
        self.num_classes = num_classes
        self.model = None

    def build_model(self):
        # Use MobileNetV2 as the base model for efficient mobile deployment
        base_model = MobileNetV2(
            input_shape=self.input_shape, include_top=False, weights="imagenet"
        )

        # Freeze the base model layers
        base_model.trainable = False

        # Build the model architecture
        self.model = models.Sequential(
            [
                base_model,
                layers.GlobalAveragePooling2D(),
                layers.Dropout(0.2),
                layers.Dense(1024, activation="relu"),
                layers.Dropout(0.2),
                layers.Dense(self.num_classes, activation="softmax"),
            ]
        )

        # Compile the model
        self.model.compile(
            optimizer="adam", loss="categorical_crossentropy", metrics=["accuracy"]
        )

        return self.model

    def train(self, train_dir, validation_dir, batch_size=32, epochs=20):
        # Data augmentation for training
        train_datagen = ImageDataGenerator(
            rescale=1.0 / 255,
            rotation_range=20,
            width_shift_range=0.2,
            height_shift_range=0.2,
            horizontal_flip=True,
            fill_mode="nearest",
        )

        # Only rescaling for validation
        validation_datagen = ImageDataGenerator(rescale=1.0 / 255)

        # Create data generators
        train_generator = train_datagen.flow_from_directory(
            train_dir,
            target_size=self.input_shape[:2],
            batch_size=batch_size,
            class_mode="categorical",
        )

        validation_generator = validation_datagen.flow_from_directory(
            validation_dir,
            target_size=self.input_shape[:2],
            batch_size=batch_size,
            class_mode="categorical",
        )

        # Train the model
        history = self.model.fit(
            train_generator,
            steps_per_epoch=train_generator.samples // batch_size,
            epochs=epochs,
            validation_data=validation_generator,
            validation_steps=validation_generator.samples // batch_size,
        )

        return history

    def save_model(self, model_path):
        """Guarda el modelo en formato SavedModel y lo convierte a TensorFlow Lite"""
        # Guardar en formato SavedModel (carpeta)
        saved_model_dir = model_path.replace(".h5", "_savedmodel")
        self.model.export(saved_model_dir)

        # Convertir a TFLite desde la carpeta SavedModel
        converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
        tflite_model = converter.convert()

        # Guardar el modelo TFLite (como archivo, no carpeta)
        tflite_model_path = saved_model_dir.rstrip("/\\") + ".tflite"
        with open(tflite_model_path, "wb") as f:
            f.write(tflite_model)

        print(f"Modelo SavedModel guardado en: {saved_model_dir}")
        print(f"Modelo TFLite guardado en: {tflite_model_path}")
        return tflite_model_path

    def predict(self, image_path):
        """Make predictions on a single image"""
        img = tf.keras.preprocessing.image.load_img(
            image_path, target_size=self.input_shape[:2]
        )
        img_array = tf.keras.preprocessing.image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array /= 255.0

        predictions = self.model.predict(img_array)
        return predictions
