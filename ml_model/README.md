# BiodiversityWatch Image Classification Model

Este módulo contiene el modelo de aprendizaje automático para la clasificación de especies en el proyecto BiodiversityWatch. El modelo está diseñado para identificar especies locales del PNN Los Nevados usando imágenes capturadas desde la aplicación móvil.

## Características

- Transferencia de aprendizaje usando MobileNetV2 para despliegue eficiente en móvil
- Aumentación de datos para mejorar la generalización
- Conversión a TensorFlow Lite para despliegue móvil
- Soporte para flora y fauna
- Integración con datasets de iNaturalist y Herbario Nacional Colombiano

## Estructura de directorios

```
ml_model/
├── data/               # Almacenamiento de datos (imágenes originales y procesadas)
├── models/             # Modelos entrenados y exportados
├── notebooks/          # Notebooks para experimentación
├── src/                # Código fuente (modelos, utilidades, scripts)
│   ├── model.py        # Implementación principal del modelo
│   ├── data_preprocessing.py  # Utilidades de preprocesamiento
│   └── data_downloader.py     # Descarga de datos desde iNaturalist
├── train_model.py      # Script para entrenamiento
├── predict.py          # Script para predicción
└── requirements.txt    # Dependencias de Python
```

## Buenas prácticas con `.gitignore`

Para mantener tu repositorio limpio y profesional, asegúrate de ignorar:

- **Entornos virtuales:**
  - `venv/`, `.env/`, `.venv/`, `env/`
- **Archivos temporales de Python:**
  - `__pycache__/`, `*.pyc`, `*.pyo`, `*.pyd`
- **Checkpoints de Jupyter:**
  - `.ipynb_checkpoints/`
- **Configuraciones de editores:**
  - `.vscode/`, `.idea/`
- **Modelos y datos generados:**
  - `ml_model/models/` (modelos entrenados)
  - `ml_model/data/processed/` (datos procesados)
  - `ml_model/data/*.jpg`, `ml_model/data/*.png` (imágenes descargadas)
- **Archivos de sistema:**
  - `.DS_Store`, `Thumbs.db`

Ejemplo de `.gitignore` recomendado:

```
# Entornos virtuales
venv/
.env/
.venv/
env/

# Archivos temporales de Python
__pycache__/
*.pyc
*.pyo
*.pyd

# Checkpoints de Jupyter
.ipynb_checkpoints/

# Configuración de editores
.vscode/
.idea/

# Modelos y datos generados
ml_model/models/
ml_model/data/processed/
ml_model/data/*.jpg
ml_model/data/*.png

# Archivos de sistema
.DS_Store
Thumbs.db
```

## Configuración

1. Crea un entorno virtual:

```bash
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate
```

2. Instala las dependencias:

```bash
pip install -r requirements.txt
```

## Uso

1. Prepara tu dataset:

   - Organiza imágenes en carpetas por especie
   - Usa las utilidades de preprocesamiento para dividir en train/val/test

2. Entrena el modelo:

```python
from src.model import SpeciesClassifier

# Inicializar el modelo
clasificador = SpeciesClassifier(num_classes=NUMERO_DE_ESPECIES)

# Construir el modelo
clasificador.build_model()

# Entrenar el modelo
historial = clasificador.train(
    train_dir='data/train',
    validation_dir='data/val',
    batch_size=32,
    epochs=20
)

# Guardar el modelo
clasificador.save_model('models/clasificador_especies.h5')
```

3. Haz predicciones:

```python
# predict.py ya automatiza la carga de especies y el modelo
python predict.py
```

## Integración con backend

- Puedes exponer el modelo como un microservicio REST en Python (Flask/FastAPI) y consumirlo desde tu backend Java/Spring Boot.
- O usar el modelo `.tflite` directamente en aplicaciones móviles.

## Buenas prácticas

- Mantén tu `.gitignore` actualizado para evitar subir archivos innecesarios.
- No subas imágenes, modelos entrenados ni entornos virtuales al repositorio.
- Documenta cualquier cambio importante en el README.

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo LICENSE para más detalles.
