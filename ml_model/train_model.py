import os
from src.model import SpeciesClassifier
from src.data_preprocessing import organize_dataset

# Configurar rutas
BASE_DIR = "model"
DATA_DIR = os.path.join(BASE_DIR, "data")
PROCESSED_DIR = os.path.join(DATA_DIR, "processed")
MODEL_DIR = os.path.join(BASE_DIR, "models")

# Crear directorios necesarios
os.makedirs(PROCESSED_DIR, exist_ok=True)
os.makedirs(MODEL_DIR, exist_ok=True)

# Organizar los datos
print("Organizando datos...")
organize_dataset(source_dir=DATA_DIR, target_dir=PROCESSED_DIR)

# Contar número de especies
num_especies = len(
    [d for d in os.listdir(DATA_DIR) if os.path.isdir(os.path.join(DATA_DIR, d))]
)
print(f"Número de especies detectadas: {num_especies}")

# Crear y entrenar el modelo
print("Creando modelo...")
clasificador = SpeciesClassifier(num_classes=num_especies)
clasificador.build_model()

# Entrenar
print("Iniciando entrenamiento...")
historial = clasificador.train(
    train_dir=os.path.join(PROCESSED_DIR, "train"),
    validation_dir=os.path.join(PROCESSED_DIR, "val"),
    batch_size=32,
    epochs=20,
)

# Guardar el modelo
print("Guardando modelo...")
modelo_path = os.path.join(MODEL_DIR, "clasificador_especies.h5")
saved_model_dir = modelo_path.replace(".h5", "_savedmodel")
clasificador.save_model(saved_model_dir)
print(f"Modelo guardado en: {saved_model_dir}")
