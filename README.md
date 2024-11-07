<h1 align="center">Multas de estacionamiento</h1>
<h3 align="center">Trabajo Práctico Especial 2 - Grupo 2</h3>

# Requisitos

* Java 21
* Maven

# Compilado

## A mano

Para preparar el entorno de ejecución a partir del código fuente del proyecto se
deben ejecutar los siguientes comandos por consola:

```bash
$ mvn clean package
````

> Se puede especificar `-am <module> -pl` para armar el
> package de un módulo en particular

Una vez hecho esto, el módulo `api` se habrá compilado y los módulos `server` y
`client` habrán generado comprimidos con extensión `.tar.gz` en las carpetas
`target/` de cada módulo.

Para descomprimir dichos archivos, donde se encuentran los archivos de código
fuente compilados y los *shell scripts* para ejecutar el proyecto hay que
ejecutar los siguientes comandos (desde el *root* del proyecto):

```bash
# Para el modulo `server`
$ cd server/target
$ tar xzf tpe2-g2-server-1.0-SNAPSHOT-bin.tar.gz

# Para el modulo `client` (nuevamente desde el root)
$ cd client/target
$ tar xzf tpe2-g2-client-1.0-SNAPSHOT-bin.tar.gz
```

Una vez descomprimidos los archivos, dentro de cada carpeta generada se
encontrarán los *shell scripts* que permitirán poner en funcionamiento el
proyecto. Para esto hay que marcarlos como **ejecutables**:

```bash
# Para el modulo `server`
$ cd server/target/tpe2-g2-server-1.0-SNAPSHOT
$ chmod +x *.sh

# Para el modulo `client`
$ cd client/target/tpe2-g2-client-1.0-SNAPSHOT
$ chmod +x *.sh
```

Una vez hecho esto, dentro de cada carpeta (en instancias terminales diferentes)
se puede pasar a la etapa de [ejecución](#ejecución).

## Script especial

Sin embargo, como hacer esto a mano es bastante molesto, desarrollamos un
*script* que permite automatizar y facilitar esta tarea y dejar los archivos
ubicados en una posición más cómoda. Dicho *script* se llama `tpe_builder.sh` y
al ejecutarlo se puede especificar los siguientes flags:

- **`-s` o `--server`:** Solo compila, desempaqueta y reubica el módulo de server.
- **`-c` o `--client`:** Solo compila, desempaqueta y reubica el módulo de cliente.
- **`-C` o `--clean`:** Al hacer las tareas, también ejecuta `mvn clean` para compilar desde cero.
- **`-h` o `--help`:** Imprime el mensaje de ayuda.

Por lo tanto, para obtener el resultado de los pasos descriptos en
[la compilación a mano](#a-mano) hay que ejecutar lo siguiente:

```bash
$ sed -i 's/\r$//' tpe_builder.sh
$ chmod +x tpe_builder.sh
$ ./tpe_builder.sh -s -c -C
```

Los archivos resultantes se encontrarán bajo `bin/client` y `bin/server` para
los módulos de cliente y servidor respectivamente permitiendo que los *scripts*
del proyecto se puedan ejecutar más fácilmente haciendo:

```bash
$ ./bin/server/server.sh
```

# Ejecución

## Servidor

Para correr el servidor, ejecutar:

```bash
# Si se ejecutó el script especial
$ ./bin/server/server.sh

# Si se hizo la compilación a mano
$ cd server/target/tpe2-g2-server-1.0-SNAPSHOT
$ ./server.sh
```

El servidor recibe un parámetro opcional llamado `-Dinterfaces` que permite especificar en momento de ejecución las
interfaces de red que se desean utilizar. Por defecto, el valor del mismo es `127.0.0.*`.

También recibe los parámetros opcionales `-DclusterName` y `-DclusterPassword` que permiten especificar el nombre y la
contraseña del cluster de Hazelcast. Por defecto, el nombre es `g2-tpe2` y la contraseña es `g2-tpe2-pass`.

## Cliente

Para todos los comandos del cliente, se asume que se compiló el proyecto utilizando el script especial. En caso contrario
se debe reemplazar `./bin/client` por `client/target/tpe2-g2-client-1.0-SNAPSHOT` (análogo al caso del servidor).

> **Atención:** todos los scripts fueron diseñados para automáticamente moverse a la carpeta donde están ubicados por
> las dependencias que tienen con las librerías. Por lo tanto, en los parámetros de ubicaciones de archivos, se tiene
> que considerar que se está en la carpeta donde se encuentra el script. Es decir, si los archivos se encuentran en la 
> carpeta `csv` del root del proyecto, al ejecutar el script `./bin/client/queryX.sh`, la ruta se debe especificar como
> `../../csv`

Para todos los scripts de los clientes, existen unos parámetros que son comunes y obligatorios a todos los scripts. Estos
son:

- `-Daddresses`: Direcciones de los servidores de la forma `ip:puerto`. Puede haber una o más separadas por punto y coma.
- `-Dcity`: Nombre de la ciudad. Puede ser _'NYC'_ o _'CHI'_, aunque el proyecto fue diseñado para ser fácilmente
    extensible a otras ciudades que mantengan la estructura de los tres archivos CSV.
- `-DinPath`: Ruta a los archivos CSV de entrada. Se espera la existencia de tres archivos: `ticketsXXX.csv`, 
    `infractionsXXX.csv` y `agenciesXXX.csv`, siendo `XXX` el nombre de la ciudad.
- `-DoutPath`: Ruta a los archivos de salida. En dicha carpeta se generarán tres archivos:
  - `queryX.csv`: Resultado de la query X.
  - `queryX_combiner.csv`: Resultado de la query X utilizando `Combiner`.
  - `timeX.txt`: Archivo con los timestamps de ejecución de la query X.

Aparte de esto, todas las queries reciben una serie de parámetros opcionales con valores por defecto. Estos son:

- `-DclusterName`: Nombre del cluster de Hazelcast. Por defecto es `g2-tpe2`.
- `-DclusterPassword`: Contraseña del cluster de Hazelcast. Por defecto es `g2-tpe2-pass`.

### Query 1: Total de multas por infracción y agencia

Para correr la query1, se debe ejecutar:

```bash
$ ./bin/client/query1.sh -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -Dcity=ABC -DinPath=XX -DoutPath=YY
```

### Query 2: Recaudación YTD por agencia

Para correr la query2, se debe ejecutar:

```bash
$ ./bin/client/query2.sh -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -Dcity=ABC -DinPath=XX -DoutPath=YY
```

### Query 3: Porcentaje de patentes reincidentes por barrio en el rango `[from, to]`

Para correr la query3, se debe ejecutar:

```bash
$ ./bin/client/query3.sh -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -Dcity=ABC -DinPath=XX -DoutPath=YY
    -Dn=N -Dfrom=DD/MM/YYYY -Dto=DD/MM/YYYY
```

Donde:

- `-Dn`: Cantidad de multas mínima que debe tener una patente para ser considerada reincidente.
- `-Dfrom`: Fecha de inicio del rango.
- `-Dto`: Fecha de fin del rango.

> **Nota:** Las fechas de inicio y fin del rango son inclusivas.

### Query 4: Top N infracciones con mayor diferencia entre máximos y mínimos montos para una agencia

Para correr la query4, se debe ejecutar:

```bash
$ ./bin/client/query4.sh -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' -Dcity=ABC -DinPath=XX -DoutPath=YY
    -Dn=N -Dagency=AGENCY
```

Donde:

- `-Dn`: Cantidad máxima de infracciones a mostrar.
- `-Dagency`: Nombre de la agencia. Los espacios deben ser reemplazados por guiones bajos (`_`).

# Testing

Para realizar los tests, desarrollamos una serie de *scripts* que que permiten correr las queries con los archivos de
prueba y se comparan los resultados con los archivos de salida esperados. Para correr los tests, se debe ejecutar:

```bash
$ sed -i 's/\r$//' tpe_tester.sh
$ chmod +x tpe_tester.sh
$ ./tpe_tester.sh
```