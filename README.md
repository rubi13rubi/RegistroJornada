# HERRAMIENTA REGISTRO JORNADA

Esta aplicación es una solución sencilla y de código abierto para que pequeñas y medianas empresas gestionen de forma digital el <b>registro de horario laboral de sus trabajadores</b>, sin depender de aplicaciones externas. En este documento se detalla una <b>guía de instalación y uso</b>.

## Instalación

El servidor web deberá ser instalado en una <b>máquina Linux</b>. Esta guía detalla el proceso completo hasta configurar el acceso seguro a través de internet, pero también es posible instalarlo en un servidor en la red local. Una vez configurado el servidor, los usuarios se podrán conectar a la web desde su navegador en <b>cualquier dispositivo</b>.

Dependiendo de la distribución de Linux y el gestor de paquetes que se use, <b>los comandos específicos de instalación de dependencias pueden variar</b>. En esta guía se detallan los más comunes, para otras distribuciones los comandos específicos se pueden encontrar en internet. 

### 1. Instalación de Java

El servidor necesita <b>Java 17</b> para funcionar. Otras versiones de Java darán errores de compatibilidad a la hora de desplegar la aplicación.

En Ubuntu/Debian:
```
sudo apt update
sudo apt install openjdk-17-jdk -y
```

En otras distribuciones comunes:
```
sudo dnf install java-17-openjdk-devel -y
```

Tras la instalación, verifica que la versión instalada es la correcta con:
```
java -version
```

### 2. Instalación y configuración de Apache Tomcat

Tomcat será el programa encargado de desplegar la aplicación. Se usa la <b>versión 10.1.44</b>.

Para instalarlo y aplicar los permisos necesarios usa los siguientes comandos:
```
wget -c https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.44/bin/apache-tomcat-10.1.44.tar.gz
tar -xvzf apache-tomcat-10.1.44.tar.gz
sudo mv apache-tomcat-10.1.44 /opt/tomcat
sudo chmod +x /opt/tomcat/bin/*.sh
```

Crea un usuario dedicado para Tomcat y aplica los permisos necesarios:
```
sudo useradd -r -m -U -d /opt/tomcat -s /bin/false tomcat
sudo chown -R tomcat:tomcat /opt/tomcat
```

Para configurar Tomcat como servicio y que se inicie automáticamente, crea y edita el archivo de configuración del servicio:
```
sudo nano /etc/systemd/system/tomcat.service
```

Pega el siguiente contenido, ajustando la ruta de Java si es necesario:
```
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking

Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=CATALINA_HOME=/opt/tomcat

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

User=tomcat
Group=tomcat
UMask=0007
RestartSec=10
Restart=always

[Install]
WantedBy=multi-user.target
```

Guarda y cierra el archivo (`ctrl + x`), activa e inicia el servicio:
```
sudo systemctl daemon-reload
sudo systemctl enable tomcat
sudo systemctl start tomcat
```

Ahora, al usar el siguiente comando:
```
sudo systemctl status tomcat
```
<b>deberías comprobar que Tomcat se encuentra activo</b>.

Por último, para probar que Tomcat está funcionando, asegúrate que tienes el <b>puerto 8080 tcp abierto en todos los firewalls</b> y barreras de seguridad de tu servidor y desde el navegador de otra máquina, prueba:
```
http://ip_de_tu_servidor:8080
```
Si todo funciona se debería observar una página de presentación de Tomcat.

### 3. Instalación y configuración de MariaDB

MariaDB es el programa que gestiona la base de datos.

En Ubuntu/Debian:
```
sudo apt update
sudo apt install mariadb-server mariadb-client -y
```

En otras distribuciones comunes:
```
sudo dnf install -y dnf-utils
sudo dnf install -y mariadb-server
```

Tras la instalación, se debe habilitar y arrancar el servicio:
```
sudo systemctl enable mariadb
sudo systemctl start mariadb
```

Se puede comprobar que está activo con:
```
sudo systemctl status mariadb
```

Ahora, es importante ejecutar el <b>script de configuración inicial</b>:
```
sudo mysql_secure_installation
```
- Define una contraseña segura para el usuario root
- Quita los usuarios anónimos
- Deshabilita el login remoto de root
- Elimina la base de datos de test

Tras configurar MariaDB, se debe crear la base de datos y el usuario para la aplicación. Entra en la consola sql usando:
```
sudo mysql -p
```
e ingresa la contraseña de root definida anteriormente.

Dentro de la consola, pega el contenido del archivo `iniciarbbdd.sql` que se puede encontrar en el código fuente de este repositorio (o en el release para ser consistente con la versión).<br>
Presiona enter para ejecutar el último comando y <b>comprueba que todas las tablas se han creado sin errores.</b>

Para <b>crear el usuario</b>, ingresa estas 3 líneas <b>una por una</b> cambiando "usuario" y "password" por un nombre de usuario y contraseña:
```
CREATE USER 'usuario'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON registrosDB.* TO 'usuario'@'localhost';
FLUSH PRIVILEGES;
```
Si las 3 lineas se han ejecutado correctamente, puedes cerrar la consola de sql escribiendo el comando `exit` o pulsando `ctrl + c`

### 4. Conexión de MariaDB con Tomcat

El último paso en la configuración básica antes de desplegar la aplicación es instalar y configurar el <b>conector de la base de datos con Tomcat</b>.
Para instalarlo:
```
cd /opt/tomcat/lib
sudo wget https://downloads.mariadb.com/Connectors/java/latest/mariadb-java-client-2.3.0.jar
sudo chown tomcat:tomcat mariadb-java-client-2.3.0.jar
```
Para configurarlo, abre el archivo de `context.xml`:
```
sudo nano /opt/tomcat/conf/context.xml
```
Y en la parte de abajo, justo antes de la linea `</Context>` pega este texto, utilizando el usuario y contraseña definidos anteriormente:
```
<Resource name="jdbc/registrosDB" auth="Container"
          type="javax.sql.DataSource"
          maxTotal="100" maxIdle="30" maxWaitMillis="10000"
          username="usuario" password="password"
          driverClassName="org.mariadb.jdbc.Driver"
          url="jdbc:mariadb://localhost:3306/registrosDB"/>

        <CookieProcessor className="org.apache.tomcat.util.http.Rfc6265CookieProcessor" />
        <SessionCookie secure="true" httpOnly="true" path="/"/>
```
Opcionalmente, también puedes configurar justo debajo un texto de firma personalizado que saldrá en la página de inicio de sesión y en los archvos descargados. Puedes poner aquí el nombre de tu empresa. Pega este texto cambiando el texto por tu mensaje. Podrás cambiarlo o eliminarlo en cualquier momento.

```
<Parameter name="FirmaPersonalizada" value="escribe tu texto aqui" override="false"/>
```



Sal del editor nano pulsando `ctrl + x` y guarda el archivo.

### 5. Despliegue de la aplicación

<b>Para desplegar la aplicación con Tomcat</b>, se debe parar el servicio de Tomcat, introducir el archivo .war (disponible en los releases de este repositorio o compilando el código fuente) en la carpeta `/opt/tomcat/webapps`, dar los permisos necesarios al usuario tomcat y volver a iniciar el servicio. <b>Asumiendo que se tiene el archivo RegistroJornada.war en el directorio de trabajo</b> (ya sea transferido por scp o descargado directamente del repositorio), usa los siguientes comandos:
```
sudo systemctl stop tomcat
sudo mv RegistroJornada.war /opt/tomcat/webapps
sudo chown tomcat:tomcat /opt/tomcat/webapps/RegistroJornada.war
sudo chmod 777 /opt/tomcat/webapps/RegistroJornada.war
sudo systemctl start tomcat
```
<b>Si ya tenías instalada una versión anterior</b>, ejecuta estos comandos justo después de parar el servicio de Tomcat y antes de intentar mover el .war.
```
sudo rm -rf /opt/tomcat/webapps/RegistroJornada
sudo rm /opt/tomcat/webapps/RegistroJornada.war
```
Finalmente, prueba a entrar en:
```
http://ip_de_tu_servidor:8080/RegistroJornada
```
Si puedes ver la <b>página de Login</b>, la aplicación ha sido configurada correctamente. Para comprobar que la base de datos está conectada, intenta hacer login con cualquier credencial y <b>comprueba que te sale el error de "contraseña incorrecta"</b>. Si observas cualquier otro error, significa que la consulta sql no se ha realizado correctamente y por lo tanto hay algo que falla en la configuración.

Si estás usando la aplicación en una red privada, ya puedes usar la aplicación sin problemas. Sin embargo, si la aplicación está desplegada en internet, <b>no deberías introducir ninguna credencial con la conexión sin cifrar</b>, ya que los atacantes podrían ver los datos que envías. Además, dejar el puerto de Tomcat (8080) abierto a internet es también potencialmente inseguro. Si quieres configurar un acceso seguro a través de internet para la aplicación, <b>continúa a la siguiente sección.</b>

## Configuración segura

Este paso <b>solo es necesario si la aplicación está desplegada en internet</b>. En una red privada la información ya está intrínsecamente protegida de atacantes externos y no es estrictamente necesario cifrarla.

### 1. Conseguir un dominio

Para desplegar una aplicación con https (cifrado) <b>es obligatorio tener un dominio</b>. Hay muchos servicios que ofrecen dominios, pero la configuración es equivalente para todos ellos. Lo único necesario es apuntar el dominio (tanto `www.tudominio.com` como `tudominio.com`) a la dirección ip del servidor.

### 2. Cambiar los puertos abiertos en firewall

Anteriormente, para probar la aplicación, se abrió el puerto 8080. En la configuración segura, <b>este puerto debe permanecer cerrado a conexiones externas</b> de manera que sea únicamente accesible de forma interna. En su lugar, <b>se abrirá el puerto 443 (https) y el 80 (http)</b>. Tras cerrar el puerto 8080, se puede comprobar que la aplicación ya no es accesible intentando entrar en:
```
http://ip_de_tu_servidor:8080/RegistroJornada
```

### 3. Instalación y configuración de Nginx

Nginx será el encargado de gestionar las solicitudes y <b>actuar como intermediario</b> entre el usuario y la Tomcat (reverse proxy). Para instalarlo, en Ubuntu/Debian:
```
sudo apt update
sudo apt install nginx -y
```

En otras distribuciones comunes:
```
sudo yum install -y nginx
```

Ahora, usa el siguiente comando para <b>crear y editar el archivo de configuración de nginx</b>:
```
sudo nano /etc/nginx/conf.d/registrojornada.conf
```
y pega el siguiente contenido, cambiando tudominio.com por el nombre real de tu dominio:
```
server {
    listen 80;
    server_name tudominio.com www.tudominio.com;

     location / {
        proxy_pass http://127.0.0.1:8080/RegistroJornada/;  # Tomcat corre en 8080
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-Host $host;
      }
}
```

Guarda el archivo y sal del editor usando `ctrl + x`, activa el servicio y comprueba que está activo con:

```
sudo systemctl enable --now nginx
sudo systemctl status nginx
```
Para comprobar que el proxy está funcionando correctamente, intenta entrar en `tudominio.com` o `www.tudominio.com` y comprueba que carga la aplicación y puedes hacer un intento de login.

Si no eres capaz de acceder a la aplicación, puede que no tengas los puertos correspondientes abiertos en el firewall. Si nginx carga pero obtienes el error 502 Bad Gateway, es posible que tengas que <b>configurar SELinux</b> para que permita las conexiones internas del proxy. Usa el siguiente comando:
```
sudo setsebool -P httpd_can_network_connect 1
```
Si todo funciona correctamente, puedes continuar para configurar la conexión segura con https.

### 4. Instalación y configuración de Certbot

Certbot será el programa encargado de <b>conseguir los certificados</b> y debería sincronizar automáticamente todos los cambios que realice con nginx para que el reverse proxy empiece a funcionar con https.

La vía recomendada para instalar Certbot es <b>Snap</b>. Si tu distribución ya tiene snap instalado, puedes usar directamente:
```
sudo snap install core; sudo snap refresh core
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
```
Si no, antes debes <b>instalar snapd y enlazarlo</b> (para instalar snapd puedes usar otros gestores de paquetes como yum dependiendo de tu distribución):
```
sudo dnf install snapd -y
sudo systemctl enable --now snapd.socket
sudo ln -s /var/lib/snapd/snap /snap
```
Para obtener tus certificados, usa el siguiente comando introduciendo tus dominios:
```
sudo certbot --nginx -d tudominio.com -d www.tudominio.com
```
Comprueba que puedes entar en tudominio.com o www.tudominio.com. Tu navegador debería mostrar que la página está <b>cifrada por https</b>. Haz un intento de login para comprobar que todo funciona correctamente (debería devolver error de contraseña incorrecta).

Si todo funciona, <b>la aplicación ya es segura y puedes empezar a usarla</b>. Es recomendable configurar la <b>renovación automática de certificados</b> para que la aplicación nunca deje de funcionar cuando estos caduquen:
```
sudo certbot renew --dry-run
```

## Uso de la aplicación

### Primer usuario e inicio de sesión

La aplicación funciona con 2 roles de usuarios: <b>empleados y encargados</b>.
- Los <b>empleados</b> son los <b>trabajadores que registran</b> su hora de entrada y salida, y pueden ver su propio historial de registros y añadir notas a ellos.
- Los <b>encargados</b> no pueden registrar su horario. <b>Únicamente gestionan</b> la creación de nuevos usuarios y pueden acceder (pero no modificar) a los registros de cualquier empleado, así como añadir notas.

Inicialmente, se presenta una <b>página de inicio de sesión</b>. La aplicación no crea ninguna cuenta por defecto, pero necesita un encargado para empezar a funcionar. Para <b>crear el primer encargado</b>, deberás entrar en el link que hay justo debajo del login.

Una vez creado el primer usuario, <b>podrás entrar con tu usuario y contraseña</b>, lo que te llevará al <b>área personal</b>. Tanto empleados como encargados tienen su área personal, pero <b>tiene diferencias según el rol</b>.

### Área personal encargados

Los encargados tienen acceso a todas las <b>funciones necesarias para gestionar sus empleados.</b>

- <b>Crear nuevo usuario:</b> Te permitirá crear tanto nuevos empleados como encargados, introduciendo un usuario, contraseña y rol.
- <b>Descargar datos de todos los empleados:</b> Mediante un selector de fecha, puedes seleccionar un intervalo, y al hacer click en descargar, se descargará un archivo registros.zip que contiene un archivo .csv para cada empleado con sus registros en ese intervalo de fechas.
- <b>Consulta de datos recientes:</b> Selecciona el empleado que quieras consultar y haz click en consultar empleado. La tabla que hay debajo se actualizará automáticamente para mostrar los últimos registros de ese usuario.

### Área personal empleados

Los encargados tienen <b>funciones más limitadas</b>. Su tabla de registros se actualizará con sus <b>últimos registros</b> directamente al entrar en el área personal. El <b>estado</b> (trabajando o no trabajando) se muestra en la parte superior, seguido de un <b>botón de fichar</b>, que añadirá inmediatamente un registro de entrada/salida según el estado actual.

### Tabla de registros

La tabla de registros es una forma cómoda de <b>ver los últimos 20 registros</b> sin tener que descargar ningún archivo. Los encargados podrán consultarla para cualquier empleado, pero los empleados solo podrán ver la suya propia.

Por motivos de consistencia y evitar falsificaciones, <b>un registro nunca podrá ser modificado ni eliminado</b>. En caso de equivocación u olvido, se podrá usar la <b>función de notas</b>. Una nota es un mensaje de texto asociada a un registro concreto. Para ver o añadir una nota, haz click en el <b>botón "ver notas"</b> al lado del registro correspondiente. Esto abrirá un recuadro donde aparecerán todas las notas (si existen), y un recuadro de texto para <b>escribir una nota nueva.</b> Al añadirla, una nota queda asociada a la fecha/hora y usuario que la publicó, y <b>no podrá ser eliminada</b>, pero se pueden añadir tantas notas como se desee. Al no aparecer en la tabla, no se podrán añadir notas a registros anteriores a los últimos 20.

Finalmente, debajo de la tabla de registros, encontrarás un <b>botón para descargar todos los datos</b>. Esto descargará un archivo .csv con todos los registros y notas asociados al usuario que se muestra en la tabla. Esto es necesario para <b>consultar registros anteriores a los últimos 20</b> que se muestran en la tabla.

### Cambiar contraseña/Cerrar sesión

Cualquier usuario puede <b>cambiar su contraseña</b> si conoce la contraseña anterior. Esto es <b>altamente recomendable</b> para todos los usuarios la primera vez que inician sesión, ya que la contraseña será definida por el encargado que crea la cuenta.

Si no se presiona el botón de cerrar sesión y <b>simplemente se cierra la ventana del navegador</b>, la próxima vez que entres en la aplicación desde el mismo dispositivo podrás ir directamente a `tudominio.com/areapersonal.html` <b>sin necesidad de pasar por el login</b>. El cierre de sesión es <b>altamente recomendable</b> al entrar desde dispositivos ajenos.

## Futuro del proyecto / contribuciones

Algunas de las <b>características que se podrían añadir</b> en futuras versiones incluyen:
- Eliminación de cuentas por parte de encargados.
- Recuperación de contraseña a través de preguntas de seguridad.
- Resumen de las horas acumuladas en un intervalo de tiempo (por ejemplo, semanales o mensuales).
- Opción para mostrar la contraseña en los campos donde está oculta.

Si echas de menos alguna otra característica o te gustaría agregarla por tu cuenta, puedes abrir un issue en el repositorio. Sin embargo, este es un proyecto realizado por una única persona y <b>no se garantiza ninguna actualización</b>.

Si este proyecto te ha sido útil y te gustaría <b>colaborar económicamente</b>, puedes hacer click <a href="http://revolut.me/rubnj2rqp">aquí</a>.

## Contacto

Si tienes cualquier problema, puedes abrir un issue en el repositorio o contactarme a mi email ruben.lr3@gmail.com