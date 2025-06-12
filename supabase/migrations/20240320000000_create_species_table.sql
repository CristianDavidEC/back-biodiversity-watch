-- Habilitar la extensión uuid-ossp
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

-- Establecer el esquema de búsqueda por defecto
SET search_path = public;

-- 1. Eliminar la tabla existente si ya existe y quieres recrearla desde cero
DROP TABLE IF EXISTS public.species CASCADE;

-- 2. Creación de la tabla de Especies (species)
CREATE TABLE public.species (
    id_specie uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    scientific_name text UNIQUE NOT NULL,
    common_name text NOT NULL,
    type text NOT NULL,
    habitat text NOT NULL,
    size text,
    ecological_role text,
    conservation_status text,
    description text,
    distribution text,
    family text,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);

-- Crear índices para mejorar el rendimiento de búsqueda
CREATE INDEX idx_species_scientific_name ON public.species (scientific_name);
CREATE INDEX idx_species_common_name ON public.species (common_name);
CREATE INDEX idx_species_type ON public.species (type);
CREATE INDEX idx_species_habitat ON public.species (habitat);
CREATE INDEX idx_species_conservation_status ON public.species (conservation_status); 