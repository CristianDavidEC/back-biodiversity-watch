-- Habilitar la extensión para generar UUIDs aleatorios
-- (Puede que ya esté habilitada por defecto)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabla de Perfiles (User en tu diagrama)
-- Vinculada a auth.users para autenticación.
CREATE TABLE public.profiles (
  id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY, -- idUser en tu diagrama, vinculado a auth.users
  email text UNIQUE NOT NULL,                                              -- email del usuario (aunque auth.users ya lo tiene, es útil aquí)
  name text,                                                               -- name en tu diagrama
  description text,                                                        -- description en tu diagrama
  profession text,                                                         -- profession en tu diagrama
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now()
);

-- Índices para mejorar el rendimiento de búsqueda
CREATE INDEX profiles_email_idx ON public.profiles (email);
CREATE INDEX profiles_name_idx ON public.profiles (name);

-- 2. Tabla de Administradores (Admin en tu diagrama)
-- Nota: Si usas Supabase Auth para admins, considera una columna `is_admin` en `profiles`
-- o una tabla de roles separada en lugar de esta tabla directa de "admins".
-- Aquí la creo según tu diagrama. La autenticación para estos usuarios no sería vía supabase.auth() directamente.
CREATE TABLE public.admins (
  id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
  email text UNIQUE NOT NULL,
  username text UNIQUE NOT NULL,
  password text NOT NULL, -- NOTA: Nunca almacenes contraseñas en texto plano. Esto DEBERÍA ser un hash.
                          -- Si autenticas admins con supabase.auth, no necesitarías esta columna aquí.
  created_at timestamp with time zone DEFAULT now()
);

-- Índices
CREATE INDEX admins_email_idx ON public.admins (email);
CREATE INDEX admins_username_idx ON public.admins (username);


-- 4. Tabla de Observaciones (Observations en tu diagrama)
-- Relacionada con 'profiles' (idObserverUser) y 'species' (idSpecies).
CREATE TABLE public.observations (
  id_observation uuid DEFAULT uuid_generate_v4() PRIMARY KEY, -- idObservation en tu diagrama
  date date NOT NULL,                                          -- date en tu diagrama
  latitude numeric,                                            -- Parte de 'place (pair of coordinates)'
  longitude numeric,                                           -- Parte de 'place (pair of coordinates)'
  note text,                                                   -- note en tu diagrama
  state text,                                                  -- state en tu diagrama
  images text[],                                               -- URLs de imágenes de la observación (List[DataFile])
  type_observation text,                                       -- typeObservation en tu diagrama
  verification_status boolean DEFAULT FALSE,                   -- verificationStatus en tu diagrama
  similarity_percentage numeric,                               -- Porcentaje de similitud con la especie identificada
  specie_scientific_name text,                                 -- Nombre científico de la especie identificada
  specie_common_name text,                                     -- Nombre común de la especie identificada
  id_specie uuid REFERENCES public.species(id_specie) ON DELETE SET NULL, -- idSpecies en tu diagrama (si la especie se elimina, la observación puede quedarse sin referencia)
  id_observer_user uuid NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE, -- idObserverUser en tu diagrama, vinculado al perfil del usuario
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now()
);

-- Índices
CREATE INDEX observations_date_idx ON public.observations (date);
CREATE INDEX observations_id_specie_idx ON public.observations (id_specie);
CREATE INDEX observations_id_observer_user_idx ON public.observations (id_observer_user);


-- Opcional: Configurar RLS (Row Level Security) básica para cada tabla
-- Esto es crucial para la seguridad en Supabase y debe ser configurado adecuadamente.
-- Estos ejemplos son básicos y deben ser adaptados a tus necesidades de seguridad.

-- Habilitar RLS en las tablas
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admins ENABLE ROW LEVEL SECURITY; -- Si los admins son usuarios separados
ALTER TABLE public.species ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.observations ENABLE ROW LEVEL SECURITY;

-- Políticas de RLS para 'profiles'
CREATE POLICY "Public profiles are viewable by everyone."
  ON public.profiles FOR SELECT
  USING (true);

CREATE POLICY "Users can update their own profile."
  ON public.profiles FOR UPDATE
  USING (auth.uid() = id);

-- Políticas de RLS para 'admins' (ejemplo, ajustar según tu estrategia de admin)
-- Si autenticas admins con Supabase Auth, esta tabla no necesitaría políticas RLS directas para autenticación.
-- Si es solo una tabla de referencia de admins, puedes restringir el acceso.
CREATE POLICY "Admins are viewable by admins only."
  ON public.admins FOR SELECT
  USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND /* Tu lógica para identificar admin */ true)); -- Ejemplo, ajusta la condición para admin.

-- Políticas de RLS para 'species'
CREATE POLICY "Species are viewable by everyone."
  ON public.species FOR SELECT
  USING (true);

-- Asumiendo que solo los admins pueden crear, actualizar o eliminar especies
CREATE POLICY "Admins can manage species."
  ON public.species FOR ALL -- ALL incluye INSERT, UPDATE, DELETE
  USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND /* Tu lógica para identificar admin */ true))
  WITH CHECK (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND /* Tu lógica para identificar admin */ true));


-- Políticas de RLS para 'observations'
CREATE POLICY "Observations are viewable by everyone."
  ON public.observations FOR SELECT
  USING (true);

CREATE POLICY "Users can create their own observations."
  ON public.observations FOR INSERT
  WITH CHECK (auth.uid() = id_observer_user);

CREATE POLICY "Users can update their own observations."
  ON public.observations FOR UPDATE
  USING (auth.uid() = id_observer_user);

CREATE POLICY "Users can delete their own observations."
  ON public.observations FOR DELETE
  USING (auth.uid() = id_observer_user);


-- 1. Función que será llamada por el trigger
-- Esta función se ejecutará después de que un nuevo usuario se inserte en 'auth.users'.
CREATE OR REPLACE FUNCTION public.handle_new_user_profile()
RETURNS TRIGGER AS $$
BEGIN
  -- Insertar un nuevo perfil en la tabla 'public.profiles'
  -- NEW.id hace referencia al ID del usuario recién insertado en auth.users.
  -- NEW.email hace referencia al email del usuario recién insertado en auth.users.
  -- Puedes acceder a los metadatos pasados en el signUp mediante NEW.raw_user_meta_data.
  INSERT INTO public.profiles (id, email, name, description, profession)
  VALUES (
    NEW.id,
    NEW.email,
    -- Intenta obtener el nombre del registro o usa el email si no se proporciona
    COALESCE(NEW.raw_user_meta_data->>'name', NEW.email),
    -- Inicializar description y profession como nulos o valores predeterminados
    NEW.raw_user_meta_data->>'description',
    NEW.raw_user_meta_data->>'profession'
  );
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
-- SECURITY DEFINER es importante para que la función tenga los permisos necesarios
-- para insertar en public.profiles.

-- 2. Trigger que llama a la función
-- Este trigger se activará 'AFTER INSERT' en la tabla 'auth.users'.
CREATE TRIGGER on_auth_user_created_create_profile
  AFTER INSERT ON auth.users
  FOR EACH ROW
  EXECUTE PROCEDURE public.handle_new_user_profile();


-- Primero, eliminar la restricción existente
ALTER TABLE public.observations DROP CONSTRAINT observations_id_specie_fkey;

-- Luego, agregar la nueva restricción que permite NULL
ALTER TABLE public.observations ADD CONSTRAINT observations_id_specie_fkey 
    FOREIGN KEY (id_specie) REFERENCES public.species(id_specie) 
    ON DELETE SET NULL 
    ON UPDATE CASCADE;

-- Agregar nuevas columnas a la tabla observations
ALTER TABLE public.observations 
    ADD COLUMN similarity_percentage numeric,
    ADD COLUMN specie_scientific_name text,
    ADD COLUMN specie_common_name text;

-- Agregar comentarios a las nuevas columnas
COMMENT ON COLUMN public.observations.similarity_percentage IS 'Porcentaje de similitud con la especie identificada';
COMMENT ON COLUMN public.observations.specie_scientific_name IS 'Nombre científico de la especie identificada';
COMMENT ON COLUMN public.observations.specie_common_name IS 'Nombre común de la especie identificada';