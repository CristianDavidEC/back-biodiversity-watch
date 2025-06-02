import os
import numpy as np
import keras
from tensorflow.keras.preprocessing import image

# Leer nombres de especies automáticamente
train_dir = "model/data/processed/train"
especies = sorted(
    [d for d in os.listdir(train_dir) if os.path.isdir(os.path.join(train_dir, d))]
)
NUMERO_DE_ESPECIES = len(especies)

# Ruta al modelo SavedModel
saved_model_dir = "model/models/clasificador_especies_savedmodel"

# Cargar el modelo SavedModel como TFSMLayer
modelo = keras.layers.TFSMLayer(saved_model_dir, call_endpoint="serving_default")

# Ruta de la imagen a probar
imagen_prueba = "model/data/mi_prueba.jpg"  # Cambia por la ruta de tu imagen

# Preprocesar la imagen igual que en el entrenamiento
img = image.load_img(imagen_prueba, target_size=(224, 224))
img_array = image.img_to_array(img)
img_array = np.expand_dims(img_array, axis=0)
img_array = img_array / 255.0

# Hacer la predicción
prediccion = modelo(img_array)
print(f"Predicción para {imagen_prueba}: {prediccion}")

# Mostrar la clase predicha y el nombre de la especie
clase_predicha = np.argmax(prediccion)
nombre_especie = especies[clase_predicha]
print(f"Índice de clase predicha: {clase_predicha}")
print(f"La especie predicha es: {nombre_especie}")
