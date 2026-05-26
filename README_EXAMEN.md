# GymTrack Mobile App - Guía de Configuración para el Profesor

Este proyecto ha sido diseñado para ser flexible y facilitar su revisión en diferentes entornos de red.

## 1. Configuración del Servidor (Backend)
Debido a que la aplicación móvil debe conectarse a su computador, el servidor Laravel debe estar escuchando en todas las interfaces de red.

Por favor, inicie su servidor backend usando el siguiente comando:
```bash
php artisan serve --host=0.0.0.0 --port=8000
```

## 2. Configuración de la App Móvil
Para evitar tener que recompilar el código con una IP diferente, la app incluye una **función secreta de configuración**:

1. Inicie la aplicación en Android Studio (ya sea en Emulador o Celular Físico).
2. En la **Pantalla de Inicio de Sesión (Login)**, realice un **CLIC LARGO (Presión prolongada)** sobre el texto del logo "**GYM TRACK**".
3. Aparecerá un cuadro de diálogo llamado "**Configuración del Servidor**".
4. Ingrese la IP de su computador y el puerto (ejemplo: `192.168.x.x:8000` o `10.0.2.2:8000` si usa emulador).
5. Presione **Guardar**.

¡Listo! La aplicación ahora intentará comunicarse con la IP que usted ha especificado.

## 3. Características Adicionales
- **Persistencia Local**: Los registros exitosos se guardan en una base de datos SQLite interna para respaldo.
- **Validación Robusta**: El registro incluye validación de tipos de datos y manejo de errores de red detallado.
- **Diseño Premium**: Interfaz oscura con acentos vibrantes inspirada en aplicaciones modernas de fitness.
