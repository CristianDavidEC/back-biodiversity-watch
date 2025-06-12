import os
from flask import Flask, request, jsonify
import numpy as np
import keras
from tensorflow.keras.preprocessing import image
import logging
from datetime import datetime

# Configurar logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
    handlers=[logging.StreamHandler()],  # Solo consola en Railway
)
logger = logging.getLogger(__name__)

# Crear directorio para archivos temporales si no existe
TEMP_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "temp")
os.makedirs(TEMP_DIR, exist_ok=True)
logger.info(f"Directorio temporal creado en: {TEMP_DIR}")

# Leer nombres de especies automáticamente
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
train_dir = os.path.join(BASE_DIR, "model", "data", "processed", "train")

# Verificar si el directorio existe antes de leer
if os.path.exists(train_dir):
    especies = sorted(
        [d for d in os.listdir(train_dir) if os.path.isdir(
            os.path.join(train_dir, d))]
    )
    especies = ['Acaena_elongata', 'Ageratina_tinifolia', 'Aglaeactis_cupripennis', 'Agrostis_perennans', 'Anas_andium', 'Andigena_nigrirostris', 'Andiperla_willinki', 'Anisognathus_igniventris', 'Arcytophyllum_muticum', 'Atlapetes_schistaceus', 'Azorella_crenata', 'Baccharis_tricuneata', 'Bombus_rubicundus', 'Calamagrostis_effusa', 'Carex_bonplandii', 'Castilleja_fissifolia', 'Chaptalia_cordata', 'Chusquea_tessellata', 'Cinclodes_fuscus', 'Cistothorus_apolinari', 'Coeligena_helianthea', 'Coespeletia_timotensis', 'Colias_dimera', 'Conepatus_semistriatus', 'Cryptotis_colombiana', 'Cuniculus_taczanowskii', 'Dendropsophus_labialis', 'Diglossa_humeralis', 'Dinomys_branickii', 'Diplostephium_phylicoides', 'Disterigma_empetrifolium', 'Draba_litamo', 'Elleanthus_aurantiacus', 'Epidendrum_aggregatum', 'Eriocnemis_vestita', 'Espeletia_argentea', 'Espeletia_grandiflora', 'Espeletia_hartwegiana', 'Espeletia_pycnophylla', 'Espeletiopsis_corymbosa', 'Falco_sparverius', 'Festuca_dolichophylla', 'Gaultheria_anastomosans', 'Gentiana_sedifolia', 'Gentianella_corymbosa', 'Geranium_sibbaldioides', 'Geranoaetus_melanoleucus', 'Grallaria_quitensis', 'Gunnera_magellanica', 'Gynoxys_fuliginosa',
                'Halenia_weddelliana', 'Huperzia_crassa', 'Hypericum_laricifolium', 'Lachemilla_orbiculata', 'Lesbia_victoriae', 'Libanothamnus_neriifolius', 'Loricaria_complanata', 'Lupinus_alopecuroides', 'Lycalopex_culpaeus', 'Lysipomia_sphagnophila', 'Macleania_rupestris', 'Masdevallia_coccinea', 'Mazama_rufina', 'Metallura_tyrianthina', 'Muscisaxicola_alpinus', 'Mustela_frenata', 'Odocoileus_virginianus_goudotii', 'Oreotrochilus_estella', 'Ourisia_chamaedrifolia', 'Oxalis_medicaginea', 'Oxypogon_guerinii', 'Penelope_montagnii', 'Pentacalia_ledifolia', 'Pernettya_prostrata', 'Phalcoboenus_carunculatus', 'Phrygilus_unicolor', 'Plantago_rigida', 'Polylepis_quadrijuga', 'Pristimantis_bogotensis', 'Pterophanes_cyanopterus', 'Puma_concolor', 'Puya_goudotiana', 'Puya_nivalis', 'Puya_trianae', 'Ranunculus_peruvianus', 'Riama_striata', 'Scytalopus_spillmanni', 'Senecio_niveoaureus', 'Sphagnum_magellanicum', 'Stenocercus_trachycephalus', 'Sylvilagus_andinus', 'Tapirus_pinchaque', 'Thomasomys_niveipes', 'Tipula', 'Tremarctos_ornatus', 'Trichomycterus_bogotensis', 'Vaccinium_floribundum', 'Valeriana_plantaginea', 'Vanessa_virginiensis', 'Vultur_gryphus', 'Werneria_nubigena', 'Zonotrichia_capensis', 'processed']
    logger.info(f"Especies cargadas: {especies}")
else:
    logger.warning(
        f"Directorio {train_dir} no encontrado. Usando especies por defecto.")
    especies = ['Acaena_elongata', 'Ageratina_tinifolia', 'Aglaeactis_cupripennis', 'Agrostis_perennans', 'Anas_andium', 'Andigena_nigrirostris', 'Andiperla_willinki', 'Anisognathus_igniventris', 'Arcytophyllum_muticum', 'Atlapetes_schistaceus', 'Azorella_crenata', 'Baccharis_tricuneata', 'Bombus_rubicundus', 'Calamagrostis_effusa', 'Carex_bonplandii', 'Castilleja_fissifolia', 'Chaptalia_cordata', 'Chusquea_tessellata', 'Cinclodes_fuscus', 'Cistothorus_apolinari', 'Coeligena_helianthea', 'Coespeletia_timotensis', 'Colias_dimera', 'Conepatus_semistriatus', 'Cryptotis_colombiana', 'Cuniculus_taczanowskii', 'Dendropsophus_labialis', 'Diglossa_humeralis', 'Dinomys_branickii', 'Diplostephium_phylicoides', 'Disterigma_empetrifolium', 'Draba_litamo', 'Elleanthus_aurantiacus', 'Epidendrum_aggregatum', 'Eriocnemis_vestita', 'Espeletia_argentea', 'Espeletia_grandiflora', 'Espeletia_hartwegiana', 'Espeletia_pycnophylla', 'Espeletiopsis_corymbosa', 'Falco_sparverius', 'Festuca_dolichophylla', 'Gaultheria_anastomosans', 'Gentiana_sedifolia', 'Gentianella_corymbosa', 'Geranium_sibbaldioides', 'Geranoaetus_melanoleucus', 'Grallaria_quitensis', 'Gunnera_magellanica', 'Gynoxys_fuliginosa',
                'Halenia_weddelliana', 'Huperzia_crassa', 'Hypericum_laricifolium', 'Lachemilla_orbiculata', 'Lesbia_victoriae', 'Libanothamnus_neriifolius', 'Loricaria_complanata', 'Lupinus_alopecuroides', 'Lycalopex_culpaeus', 'Lysipomia_sphagnophila', 'Macleania_rupestris', 'Masdevallia_coccinea', 'Mazama_rufina', 'Metallura_tyrianthina', 'Muscisaxicola_alpinus', 'Mustela_frenata', 'Odocoileus_virginianus_goudotii', 'Oreotrochilus_estella', 'Ourisia_chamaedrifolia', 'Oxalis_medicaginea', 'Oxypogon_guerinii', 'Penelope_montagnii', 'Pentacalia_ledifolia', 'Pernettya_prostrata', 'Phalcoboenus_carunculatus', 'Phrygilus_unicolor', 'Plantago_rigida', 'Polylepis_quadrijuga', 'Pristimantis_bogotensis', 'Pterophanes_cyanopterus', 'Puma_concolor', 'Puya_goudotiana', 'Puya_nivalis', 'Puya_trianae', 'Ranunculus_peruvianus', 'Riama_striata', 'Scytalopus_spillmanni', 'Senecio_niveoaureus', 'Sphagnum_magellanicum', 'Stenocercus_trachycephalus', 'Sylvilagus_andinus', 'Tapirus_pinchaque', 'Thomasomys_niveipes', 'Tipula', 'Tremarctos_ornatus', 'Trichomycterus_bogotensis', 'Vaccinium_floribundum', 'Valeriana_plantaginea', 'Vanessa_virginiensis', 'Vultur_gryphus', 'Werneria_nubigena', 'Zonotrichia_capensis', 'processed']
# Ruta al modelo SavedModel
saved_model_dir = os.path.join(
    BASE_DIR, "model", "models", "clasificador_especies_savedmodel")

# Verificar si el modelo existe
if os.path.exists(saved_model_dir):
    modelo = keras.layers.TFSMLayer(
        saved_model_dir, call_endpoint="serving_default")
    logger.info("Modelo cargado exitosamente")
else:
    logger.error(f"Modelo no encontrado en {saved_model_dir}")
    modelo = None

app = Flask(__name__)


@app.route("/", methods=["GET"])
def health_check():
    logger.info(f"Especies disponibles: {especies}")
    logger.info(f"Cantidad de especies: {len(especies)}")
    return jsonify({
        "status": "healthy",
        "especies_disponibles": len(especies),
        "modelo_cargado": modelo is not None
    })


@app.route("/test", methods=["GET"])
def test_endpoint():
    """Ruta de prueba simple para verificar conectividad"""
    return jsonify({
        "mensaje": "¡Servicio funcionando correctamente! 🌿",
        "api": "BiodiversityWatch ML",
        "endpoints_disponibles": {
            "GET /": "Health check básico",
            "GET /test": "Prueba de conectividad",
            "POST /predict": "Predicción de especies (requiere imagen)"
        }
    })


@app.before_request
def log_request_info():
    logger.info("Headers: %s", request.headers)
    logger.info("Body: %s", request.get_data())


@app.after_request
def log_response_info(response):
    logger.info("Response Status: %s", response.status)
    logger.info("Response Headers: %s", response.headers)
    return response


@app.route("/predict", methods=["POST"])
def predict():
    logger.info("Recibida nueva request de predicción")

    if "file" not in request.files:
        logger.error("No se encontró archivo en la request")
        return jsonify({"error": "No file part"}), 400

    file = request.files["file"]
    if file.filename == "":
        logger.error("Nombre de archivo vacío")
        return jsonify({"error": "No selected file"}), 400

    logger.info(f"Procesando archivo: {file.filename}")

    # Generar nombre único para el archivo temporal
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    temp_filename = f"temp_pred_{timestamp}.jpg"
    temp_path = os.path.join(TEMP_DIR, temp_filename)

    try:
        # Guardar temporalmente la imagen
        file.save(temp_path)
        logger.info(f"Imagen guardada temporalmente en: {temp_path}")

        # Preprocesar la imagen igual que en el entrenamiento
        img = image.load_img(temp_path, target_size=(224, 224))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array = img_array / 255.0

        # Hacer la predicción
        logger.info("Realizando predicción...")
        prediccion = modelo(img_array)

        # Extraer las probabilidades del diccionario de salida
        # El modelo devuelve un diccionario con una clave que contiene las probabilidades
        probabilidades = list(prediccion.values())[0].numpy()
        clase_predicha = int(np.argmax(probabilidades))
        nombre_especie = especies[clase_predicha]
        probabilidad = float(np.max(probabilidades))

        logger.info(
            f"Predicción completada - Especie: {nombre_especie}, Probabilidad: {probabilidad:.2%}"
        )
        logger.info(f"Probabilidades completas: {probabilidades.tolist()}")

        return jsonify(
            {
                "indice": clase_predicha,
                "especie": nombre_especie,
                "probabilidades": probabilidades.tolist(),
            }
        )
    except Exception as e:
        logger.error(f"Error durante la predicción: {str(e)}", exc_info=True)
        return jsonify({"error": "Error procesando la imagen"}), 500
    finally:
        # Limpiar archivo temporal
        try:
            if os.path.exists(temp_path):
                os.remove(temp_path)
                logger.info(f"Archivo temporal eliminado: {temp_path}")
        except Exception as e:
            logger.error(f"Error al eliminar archivo temporal: {str(e)}")


if __name__ == "__main__":
    # Railway asigna un puerto dinámicamente
    port = int(os.environ.get("PORT", 8081))
    logger.info(f"Iniciando servidor Flask en puerto {port}...")
    app.run(host="0.0.0.0", port=port)
