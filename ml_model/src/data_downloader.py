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

    def download_species_data(self, species_list, min_observations=50):
        """
        Descarga imágenes de especies específicas de iNaturalist

        Args:
            species_list (list): Lista de nombres científicos de especies
            min_observations (int): Número mínimo de observaciones por especie
        """
        for species in species_list:
            print(f"Descargando datos para {species}...")

            # Crear directorio para la especie
            species_dir = os.path.join(self.output_dir, species.replace(" ", "_"))
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
                            img_path = os.path.join(species_dir, f"{obs['id']}.jpg")
                            img.save(img_path)
                            count += 1

                            # Esperar para no sobrecargar la API
                            time.sleep(0.5)

                print(f"Descargadas {count} imágenes para {species}")

            except Exception as e:
                print(f"Error descargando {species}: {str(e)}")

    def download_pnn_nevados_data(self):
        """
        Descarga datos específicos del PNN Los Nevados
        """
        # Coordenadas aproximadas del PNN Los Nevados
        bounds = {"nelat": 4.9, "nelng": -75.4, "swlat": 4.7, "swlng": -75.6}

        # Especies comunes en el PNN Los Nevados
        especies = [
            "Polylepis quadrijuga",
            "Espeletia hartwegiana",
            "Puya goudotiana",
            "Calamagrostis effusa",
            "Arachniodes denticulata",
        ]

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
                species_dir = os.path.join(self.output_dir, especie.replace(" ", "_"))
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

    # Descargar datos del PNN Los Nevados
    print("Descargando datos del PNN Los Nevados...")
    downloader.download_pnn_nevados_data()

    # Descargar datos de especies específicas
    especies = [
        "Polylepis quadrijuga",
        "Espeletia hartwegiana",
        "Puya goudotiana",
        "Calamagrostis effusa",
        "Arachniodes denticulata",
    ]
    downloader.download_species_data(especies)
