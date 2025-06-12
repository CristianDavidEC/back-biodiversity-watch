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
    logger.info(f"Especies cargadas: {especies}")
else:
    logger.warning(
        f"Directorio {train_dir} no encontrado. Usando especies por defecto.")
    especies = [
        # --- PLANTAS ---
        # Frailejones y relacionados (Asteraceae)
        "Espeletia grandiflora",        # Frailejón común/de oso
        "Espeletia hartwegiana",       # Frailejón de Hartweg
        "Espeletia pycnophylla",       # Frailejón de hojas densas
        "Espeletia argentea",          # Frailejón plateado
        "Espeletiopsis corymbosa",     # Frailejón falso
        # (Aunque más de Venezuela, algunas poblaciones/híbridos en Colombia)
        "Coespeletia timotensis",
        "Libanothamnus neriifolius",   # Tabacote morado
        "Pentacalia ledifolia",        # Chilca de páramo
        "Baccharis tricuneata",        # Romerito de páramo
        "Gynoxys fuliginosa",          # Árnica de páramo
        "Senecio niveoaureus",         # Senecio dorado
        "Werneria nubigena",           # Werneria de las nubes
        "Loricaria complanata",        # Loricaria
        "Diplostephium phylicoides",   # Falso romero
        "Ageratina tinifolia",         # Salvia amarga de páramo
        # Valeriana de páramo (no es Valeriana real)
        "Chaptalia cordata",

        # Gramíneas y Ciperáceas (Poaceae, Cyperaceae)
        "Calamagrostis effusa",        # Pajonal de páramo
        "Chusquea tessellata",         # Chusque o bambú de páramo
        "Festuca dolichophylla",       # Festuca de páramo
        "Agrostis perennans",          # Pasto de páramo
        "Carex bonplandii",            # Cortadera de Bonpland
        "Rhynchospora paramora",       # Rhynchospora de páramo

        # Rosáceas (Rosaceae)
        # Colorado, Siete Cueros de páramo (árbol de mayor altitud)
        "Polylepis quadrijuga",
        "Acaena elongata",             # Cadillo de páramo
        "Lachemilla orbiculata",       # Guardarocío

        # Ericáceas (Ericaceae) - Arándanos y parientes
        "Vaccinium floribundum",       # Mortiño, agraz de páramo
        "Pernettya prostrata",         # Arrayancillo
        "Gaultheria anastomosans",     # Gaultheria
        "Macleania rupestris",         # Uva camarona
        "Disterigma empetrifolium",    # Disterigma

        # Puyas (Bromeliaceae)
        "Puya goudotiana",             # Puya de Goudot
        "Puya nivalis",                # Puya de las nieves
        "Puya trianae",                # Puya de Triana

        # Otras familias de plantas
        "Hypericum laricifolium",      # Chite, corazoncillo
        "Gentiana sedifolia",          # Genciana de páramo
        "Gentianella corymbosa",       # Gencianela
        "Halenia weddelliana",         # Halenia
        "Valeriana plantaginea",       # Valeriana de páramo
        "Lupinus alopecuroides",       # Lupino de páramo, chocho de páramo
        "Draba litamo",                # Draba del Litamo (endemismo)
        # Llantén de páramo (formador de cojines)
        "Plantago rigida",
        "Azorella crenata",            # Azorella (formador de cojines)
        "Arcytophyllum muticum",       # Coralito de páramo
        "Castilleja fissifolia",       # Pincelito de páramo
        "Calceolaria herbeohybrida",   # Zapatito de páramo
        "Ourisia chamaedrifolia",      # Ourisia
        "Lysipomia sphagnophila",      # Lisipomia de musgo
        "Gunnera magellanica",         # Ruibarbo de páramo (pequeño)
        "Ranunculus peruvianus",       # Botón de oro de páramo
        "Geranium sibbaldioides",      # Geranio de páramo
        "Oxalis medicaginea",          # Vinagrillo de páramo
        "Jamesonia bogotensis",        # Helecho de cuero
        "Huperzia crassa",             # Licopodio de páramo
        "Isoetes palmeri",             # Isoetes (en lagunas)
        "Sphagnum magellanicum",       # Musgo de turbera

        # Orquídeas de altura
        "Epidendrum aggregatum",       # Orquídea de páramo
        # Masdevallia roja (puede encontrarse en zonas altas)
        "Masdevallia coccinea",
        "Elleanthus aurantiacus",      # Elleanthus naranja

        # --- MAMÍFEROS ---
        "Tremarctos ornatus",          # Oso de anteojos, oso andino
        "Tapirus pinchaque",           # Danta de montaña, tapir andino
        "Puma concolor",               # Puma
        "Odocoileus virginianus goudotii",  # Venado de cola blanca de páramo
        "Mazama rufina",               # Venado soche o colorado pequeño
        "Pudu mephistophiles",         # Pudú del norte, venado conejo
        "Lycalopex culpaeus",          # Zorro culpeo andino
        "Mustela frenata",             # Comadreja de cola larga
        "Conepatus semistriatus",      # Zorrillo o mapurite de páramo
        # Conejo de páramo (antes S. brasiliensis andinus)
        "Sylvilagus andinus",
        "Cuniculus taczanowskii",      # Tinajo o paca de montaña
        # Pacarana o Guagua de cola ancha (bordes de páramo)
        "Dinomys branickii",
        "Thomasomys niveipes",         # Ratón de páramo de patas blancas
        # Ratón de agua de Orces (especialista de arroyos)
        "Chibchanomys orcesi",
        "Cryptotis colombiana",        # Musaraña colombiana de páramo
        "Coendou rufescens",           # Puercoespín de cola corta o erizo rojo

        # --- AVES ---
        "Vultur gryphus",              # Cóndor de los Andes
        "Geranoaetus melanoleucus",    # Águila mora
        "Phalcoboenus carunculatus",   # Caracara paramuno
        "Falco sparverius",            # Cernícalo americano
        # Tucán andino de pico negro (Terlaque Andino)
        "Andigena nigrirostris",
        "Penelope montagnii",          # Pava andina
        "Anas andium",                 # Pato andino, cerceta andina
        # Colibrí chivito de páramo bogotano (Bearded Helmetcrest)
        "Oxypogon guerinii",
        # Colibrí estrella ecuatoriano (puede llegar a páramos del sur de Col.)
        "Oreotrochilus estella",
        "Aglaeactis cupripennis",      # Colibrí rayito de sol brillante
        "Lesbia victoriae",            # Colibrí coludo azul
        "Eriocnemis vestita",          # Calzadito reluciente
        "Pterophanes cyanopterus",     # Colibrí gigante de alas azules
        "Coeligena helianthea",        # Inca ventriazul
        "Metallura tyrianthina",       # Metalura tiria
        "Grallaria quitensis",         # Tororoi leonado (Tawny Antpitta)
        "Scytalopus spillmanni",       # Tapaculo de Spillmann
        # Cinclodes alifranjeado (Bar-winged Cinclodes)
        "Cinclodes fuscus",
        "Muscisaxicola alpinus",       # Dormilona alpina
        # Cucarachero de pantano de Apolinar (endémico)
        "Cistothorus apolinari",
        "Zonotrichia capensis",        # Copetón, gorrión de collar rufo
        "Phrygilus unicolor",          # Fringilo plomizo
        "Diglossa humeralis",          # Pinchaflor negro
        "Anisognathus igniventris",    # Tangara montana ventriescarlata
        "Atlapetes schistaceus",       # Gorrión montés pizarroso

        # --- ANFIBIOS --- (Muy afectados por quitridiomicosis)
        "Pristimantis bogotensis",     # Coquí de Bogotá (rana de lluvia)
        # Rana arlequín de Muisca (CR, posiblemente extinta)
        "Atelopus muisca",
        "Dendropsophus labialis",      # Ranita verde de quebrada

        # --- REPTILES ---
        "Stenocercus trachycephalus",  # Lagartija de collar espinosa
        "Riama striata",               # Lagartija listada de páramo

        # --- INSECTOS --- (Extremadamente diversos, algunos ejemplos)
        "Bombus rubicundus",           # Abejorro de páramo
        "Colias dimera",               # Mariposa amarilla de los Andes
        # Mariposa Vanesa americana (llega a altura)
        "Vanessa virginiensis",
        # Plecóptero de glaciares y arroyos muy fríos (género, especie varía)
        "Andiperla willinki",
        # Zancudos gigantes de páramo (muchas especies)
        "Tipula (género)",

        # --- PECES --- (Poca diversidad nativa en aguas de páramo estricto)
        # Capitán de la sabana, pez gato de los Andes (puede estar en corrientes altas)
        "Astroblepus grixalvii",
        # Guapucha (en sistemas acuáticos conectados al páramo)
        "Trichomycterus bogotensis"
    ]

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
    port = int(os.environ.get("PORT", 8082))
    logger.info(f"Iniciando servidor Flask en puerto {port}...")
    app.run(host="0.0.0.0", port=port)
