# Manual de Instalación y Uso - Biodiversity Watch

Este manual proporciona instrucciones detalladas para la instalación, configuración y uso del sistema de reconocimiento de especies de Biodiversity Watch.

## Requisitos Previos

- Python 3.8 o superior
- pip (gestor de paquetes de Python)
- Git
- Conexión a Internet
- Espacio en disco: mínimo 2GB (dependiendo del tamaño del dataset)

## 1. Instalación del Entorno

### 1.1 Clonar el Repositorio

```bash
git clone https://github.com/CristianDavidEC/back-biodiversity-watch.git
cd back-biodiversity-watch/ml_model
```

### 1.2 Crear y Activar Entorno Virtual

```bash
# Windows
python -m venv .venv
.venv\Scripts\activate

# Linux/Mac
python3 -m venv .venv
source .venv/bin/activate
```

### 1.3 Instalar Dependencias

```bash
pip install -r requirements.txt
```

## 2. Preparación del Dataset

### 2.1 Estructura de Directorios

Crear la siguiente estructura de directorios:

```
ml_model/
└── model/
    └── data/
        ├── [especie1]/
        ├── [especie2]/
        └── ...
```

### 2.2 Organización de Imágenes

- Colocar las imágenes de cada especie en su respectiva carpeta dentro de `model/data/`
- Las imágenes deben estar en formato JPG o PNG
- Se recomienda tener al menos 50 imágenes por especie para un buen entrenamiento

## 3. Entrenamiento del Modelo

### 3.1 Ejecutar el Entrenamiento

```bash
python train_model.py
```

El script realizará las siguientes acciones:

1. Organizará el dataset en conjuntos de entrenamiento y validación
2. Entrenará el modelo de clasificación
3. Guardará el modelo entrenado en `model/models/`

### 3.2 Monitoreo del Entrenamiento

- El progreso del entrenamiento se mostrará en la consola
- Se generarán métricas de rendimiento
- El modelo se guardará automáticamente al finalizar

## 4. Ejecución de la API Local

### 4.1 Iniciar el Servidor

```bash
python api.py
```

La API estará disponible en:

- URL local: `http://localhost:5000`
- Endpoint de predicción: `http://localhost:5000/predict`

### 4.2 Conexión con la Aplicación Móvil

1. Asegúrate de que el teléfono y la computadora estén en la misma red WiFi
2. Obtén la dirección IP local de tu computadora:
   - Windows: `ipconfig` en CMD
   - Linux/Mac: `ifconfig` en Terminal
3. En la aplicación móvil, configura la URL del servidor como:
   `http://[TU_IP_LOCAL]:5000`

## 5. Solución de Problemas

### 5.1 Errores Comunes

1. **Error de dependencias faltantes**

   ```bash
   pip install -r requirements.txt --upgrade
   ```

2. **Error de memoria durante el entrenamiento**

   - Reducir el tamaño del batch en `train_model.py`
   - Reducir el tamaño de las imágenes

3. **Error de conexión con la API**
   - Verificar que el firewall no esté bloqueando el puerto 5000
   - Confirmar que la IP local sea correcta

### 5.2 Logs y Depuración

- Los logs de la API se guardan en `api.log`
- Para más detalles durante el entrenamiento, revisar la consola

## 6. Mantenimiento

### 6.1 Actualización del Modelo

1. Agregar nuevas imágenes al dataset
2. Ejecutar nuevamente `train_model.py`
3. El nuevo modelo reemplazará al anterior

### 6.2 Limpieza de Datos

- Eliminar imágenes de baja calidad
- Mantener un balance entre especies
- Verificar la correcta clasificación de las imágenes

## 7. Recursos Adicionales

- [Documentación de TensorFlow](https://www.tensorflow.org/)
- [Documentación de Flask](https://flask.palletsprojects.com/)
- [Guía de buenas prácticas en ML](https://developers.google.com/machine-learning/guides/rules-of-ml)

## Contacto y Soporte

Para reportar problemas o solicitar ayuda:

- Crear un issue en el repositorio
- Contactar al equipo de desarrollo

---

**Nota**: Este manual se actualizará periódicamente. Asegúrate de estar usando la última versión.
