import os
import shutil
import pandas as pd
from sklearn.model_selection import train_test_split
import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator


def organize_dataset(source_dir, target_dir, test_size=0.2, val_size=0.2):
    """
    Organize the dataset into train, validation, and test sets

    Args:
        source_dir (str): Directory containing the original dataset
        target_dir (str): Directory where the organized dataset will be saved
        test_size (float): Proportion of data to use for testing
        val_size (float): Proportion of training data to use for validation
    """
    # Create necessary directories
    for split in ["train", "val", "test"]:
        os.makedirs(os.path.join(target_dir, split), exist_ok=True)

    # Get all class directories
    classes = [
        d for d in os.listdir(source_dir) if os.path.isdir(os.path.join(source_dir, d))
    ]

    print("\nProcesando clases:")
    for class_name in classes:
        print(f"\nProcesando clase: {class_name}")
        # Create class directories in train, val, and test
        for split in ["train", "val", "test"]:
            os.makedirs(os.path.join(target_dir, split,
                        class_name), exist_ok=True)

        # Get all images for this class
        class_dir = os.path.join(source_dir, class_name)
        images = [
            f for f in os.listdir(class_dir) if f.lower().endswith((".jpg", ".jpeg", ".png"))
        ]

        # Saltar si la carpeta está vacía
        if len(images) == 0:
            print(
                f"[AVISO] Carpeta vacía para la clase '{class_name}', se omite.")
            continue

        print(f"Total de imágenes encontradas: {len(images)}")

        # Si hay muy pocas imágenes, ponerlas todas en entrenamiento
        if len(images) <= 3:
            print(
                f"[AVISO] Clase '{class_name}' tiene muy pocas imágenes ({len(images)}). Todas irán a entrenamiento.")
            for img in images:
                shutil.copy2(
                    os.path.join(class_dir, img),
                    os.path.join(target_dir, "train", class_name, img),
                )
            continue

        # Para clases con más de 3 imágenes, hacer la división normal
        try:
            # Primero dividir en train y test
            train_imgs, test_imgs = train_test_split(
                images, test_size=test_size, random_state=42
            )

            # Luego dividir train en train y validation
            train_imgs, val_imgs = train_test_split(
                train_imgs, test_size=val_size, random_state=42
            )

            # Copy images to their respective directories
            for img in train_imgs:
                shutil.copy2(
                    os.path.join(class_dir, img),
                    os.path.join(target_dir, "train", class_name, img),
                )

            for img in val_imgs:
                shutil.copy2(
                    os.path.join(class_dir, img),
                    os.path.join(target_dir, "val", class_name, img),
                )

            for img in test_imgs:
                shutil.copy2(
                    os.path.join(class_dir, img),
                    os.path.join(target_dir, "test", class_name, img),
                )

            print(f"Distribución de imágenes para {class_name}:")
            print(f"- Entrenamiento: {len(train_imgs)}")
            print(f"- Validación: {len(val_imgs)}")
            print(f"- Prueba: {len(test_imgs)}")

        except Exception as e:
            print(f"[ERROR] Error procesando clase '{class_name}': {str(e)}")
            print("Poniendo todas las imágenes en entrenamiento...")
            for img in images:
                shutil.copy2(
                    os.path.join(class_dir, img),
                    os.path.join(target_dir, "train", class_name, img),
                )


def create_data_generators(
    train_dir, val_dir, test_dir, batch_size=32, target_size=(224, 224)
):
    """
    Create data generators for training, validation, and testing

    Args:
        train_dir (str): Directory containing training images
        val_dir (str): Directory containing validation images
        test_dir (str): Directory containing test images
        batch_size (int): Batch size for training
        target_size (tuple): Target size for images

    Returns:
        tuple: (train_generator, validation_generator, test_generator)
    """
    # Data augmentation for training
    train_datagen = ImageDataGenerator(
        rescale=1.0 / 255,
        rotation_range=20,
        width_shift_range=0.2,
        height_shift_range=0.2,
        horizontal_flip=True,
        fill_mode="nearest",
    )

    # Only rescaling for validation and test
    test_datagen = ImageDataGenerator(rescale=1.0 / 255)

    # Create generators
    train_generator = train_datagen.flow_from_directory(
        train_dir,
        target_size=target_size,
        batch_size=batch_size,
        class_mode="categorical",
    )

    validation_generator = test_datagen.flow_from_directory(
        val_dir,
        target_size=target_size,
        batch_size=batch_size,
        class_mode="categorical",
    )

    test_generator = test_datagen.flow_from_directory(
        test_dir,
        target_size=target_size,
        batch_size=batch_size,
        class_mode="categorical",
    )

    return train_generator, validation_generator, test_generator
