import os
import requests
import json
import pandas as pd
from PIL import Image
from io import BytesIO
import time


class DataDownloader:
    def __init__(self, output_dir="model/data"):
        self.output_dir = output_dir
        self.api_url = "https://api.inaturalist.org/v1/observations"
        self.headers = {"User-Agent": "BiodiversityWatch/1.0"}

    def download_species_data(self, species_list, min_observations=15):
        """
        Descarga imágenes de especies específicas de iNaturalist

        Args:
            species_list (list): Lista de nombres científicos de especies
            min_observations (int): Número mínimo de observaciones por especie
        """
        for species in species_list:
            print(f"Descargando datos para {species}...")

            # Crear directorio para la especie
            species_dir = os.path.join(
                self.output_dir, species.replace(" ", "_"))
            os.makedirs(species_dir, exist_ok=True)

            # Parámetros de búsqueda
            params = {
                "taxon_name": species,
                "quality_grade": "research",  # Solo observaciones verificadas
                "per_page": 200,
                "order": "desc",
                "order_by": "created_at",
            }

            try:
                # Obtener observaciones
                response = requests.get(
                    self.api_url, params=params, headers=self.headers
                )
                data = response.json()

                # Descargar imágenes
                count = 0
                for obs in data.get("results", []):
                    if count >= min_observations:
                        break

                    # Obtener URL de la imagen
                    if "photos" in obs and obs["photos"]:
                        photo_url = obs["photos"][0]["url"]

                        # Descargar imagen
                        img_response = requests.get(photo_url)
                        if img_response.status_code == 200:
                            # Guardar imagen
                            img = Image.open(BytesIO(img_response.content))
                            img_path = os.path.join(
                                species_dir, f"{obs['id']}.jpg")
                            img.save(img_path)
                            count += 1

                            # Esperar para no sobrecargar la API
                            time.sleep(0.5)

                print(f"Descargadas {count} imágenes para {species}")

            except Exception as e:
                print(f"Error descargando {species}: {str(e)}")

    def download_pnn_nevados_data(self, especies):
        """
        Descarga datos específicos del PNN Los Nevados
        """
        # Coordenadas aproximadas del PNN Los Nevados
        bounds = {"nelat": 4.9, "nelng": -75.4, "swlat": 4.7, "swlng": -75.6}

        for especie in especies:
            print(f"Descargando datos para {especie} en PNN Los Nevados...")

            params = {
                "taxon_name": especie,
                "quality_grade": "research",
                "per_page": 200,
                "nelat": bounds["nelat"],
                "nelng": bounds["nelng"],
                "swlat": bounds["swlat"],
                "swlng": bounds["swlng"],
            }

            try:
                response = requests.get(
                    self.api_url, params=params, headers=self.headers
                )
                data = response.json()

                # Crear directorio para la especie
                species_dir = os.path.join(
                    self.output_dir, especie.replace(" ", "_"))
                os.makedirs(species_dir, exist_ok=True)

                # Descargar imágenes
                count = 0
                for obs in data.get("results", []):
                    if "photos" in obs and obs["photos"]:
                        photo_url = obs["photos"][0]["url"]
                        img_response = requests.get(photo_url)

                        if img_response.status_code == 200:
                            img = Image.open(BytesIO(img_response.content))
                            img_path = os.path.join(
                                species_dir, f"nevados_{obs['id']}.jpg"
                            )
                            img.save(img_path)
                            count += 1
                            time.sleep(0.5)

                print(f"Descargadas {count} imágenes para {especie}")

            except Exception as e:
                print(f"Error descargando {especie}: {str(e)}")


# Ejemplo de uso
if __name__ == "__main__":
    downloader = DataDownloader()

    paramo_species_colombia = [
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
        "Tipula",

        # --- PECES --- (Poca diversidad nativa en aguas de páramo estricto)
        # Capitán de la sabana, pez gato de los Andes (puede estar en corrientes altas)
        "Astroblepus grixalvii",
        # Guapucha (en sistemas acuáticos conectados al páramo)
        "Trichomycterus bogotensis"
    ]
    # Descargar datos del PNN Los Nevados
    print("Descargando datos del PNN Los Nevados...")
    # downloader.download_pnn_nevados_data(paramo_species_colombia)

    # Descargar datos de especies específicas

    downloader.download_species_data(paramo_species_colombia)
