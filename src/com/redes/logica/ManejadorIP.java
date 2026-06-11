package com.redes.logica;
import java.util.Collections;
import java.util.List;
public class ManejadorIP {

    // 1. Convierte una IP como "192.168.1.0" a un número entero de 32 bits (usamos long para evitar negativos en Java)
    public static long ipAEntero(String ipAddress) {
        String[] octetos = ipAddress.split("\\.");
        long resultado = 0;
        for (int i = 0; i < 4; i++) {
            resultado += Long.parseLong(octetos[i]) << (24 - (8 * i));
        }
        return resultado;
    }

    // 2. Convierte el número de 32 bits de regreso al formato "192.168.1.0"
    public static String enteroAIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    // 3. Formatea el número a binario con puntos (ej. 11000000.10101000...) para mostrar el "subneteo"
    public static String enteroABinarioString(long ip) {
        String binario = String.format("%32s", Long.toBinaryString(ip)).replace(' ', '0');
        return binario.substring(0, 8) + "." + binario.substring(8, 16) + "." +
                binario.substring(16, 24) + "." + binario.substring(24, 32);
    }

    // 4. EL CÁLCULO CIDR CON EL PASO A PASO DETALLADO
    public static String calcularCIDRPasoAPaso(String ipOrigen, int prefijo) {
        StringBuilder pasos = new StringBuilder();

        // Matemáticas de red a nivel de bits
        long ipBase = ipAEntero(ipOrigen);
        long mascara = (0xFFFFFFFFL << (32 - prefijo)) & 0xFFFFFFFFL;
        long red = ipBase & mascara; // Operación AND
        long broadcast = red | (~mascara & 0xFFFFFFFFL); // Operación OR con máscara invertida
        long primeraIp = red + 1;
        long ultimaIp = broadcast - 1;
        int hostsDisponibles = (int) (broadcast - red - 1);

        // Construcción del reporte paso a paso
        pasos.append("=== ANÁLISIS CIDR PARA ").append(ipOrigen).append("/").append(prefijo).append(" ===\n\n");

        pasos.append("PASO 1: CONVERSIÓN A BINARIO\n");
        pasos.append("IP Decimal : ").append(ipOrigen).append("\n");
        pasos.append("IP Binario : ").append(enteroABinarioString(ipBase)).append("\n\n");

        pasos.append("PASO 2: CÁLCULO DE MÁSCARA DE SUBRED\n");
        pasos.append("Prefijo /").append(prefijo).append(" significa ").append(prefijo).append(" bits encendidos (1) de izquierda a derecha.\n");
        pasos.append("Masc. Bin  : ").append(enteroABinarioString(mascara)).append("\n");
        pasos.append("Masc. Dec  : ").append(enteroAIp(mascara)).append("\n\n");

        pasos.append("PASO 3: DIRECCIÓN DE RED (Operación AND lógico)\n");
        pasos.append("IP Binario : ").append(enteroABinarioString(ipBase)).append("\n");
        pasos.append("Masc Bin   : ").append(enteroABinarioString(mascara)).append("\n");
        pasos.append("-------------------------------------------------\n");
        pasos.append("Red Binario: ").append(enteroABinarioString(red)).append("\n");
        pasos.append("Red Decimal: ").append(enteroAIp(red)).append("\n\n");

        pasos.append("PASO 4: DIRECCIÓN DE BROADCAST (Operación OR con bits de host)\n");
        pasos.append("Los ").append(32 - prefijo).append(" bits de host a la derecha se vuelven 1.\n");
        pasos.append("Bcast Bin  : ").append(enteroABinarioString(broadcast)).append("\n");
        pasos.append("Bcast Dec  : ").append(enteroAIp(broadcast)).append("\n\n");

        pasos.append("PASO 5: RANGO DE DIRECCIONES ASIGNABLES\n");
        pasos.append("Primera IP : ").append(enteroAIp(primeraIp)).append(" (Red + 1)\n");
        pasos.append("Última IP  : ").append(enteroAIp(ultimaIp)).append(" (Broadcast - 1)\n");
        pasos.append("Fórmula    : 2^").append(32 - prefijo).append(" - 2\n");
        pasos.append("Total Hosts: ").append(hostsDisponibles).append(" hosts utilizables.\n");

        return pasos.toString();
    }


    // 5. EL CÁLCULO VLSM CON EL PASO A PASO Y TABLA RESUMEN
    public static String calcularVLSMPasoAPaso(String ipOrigen, int prefijoBase, List<Integer> hostsRequeridos) {
        StringBuilder reporte = new StringBuilder();
        StringBuilder tabla = new StringBuilder();
        StringBuilder desglose = new StringBuilder();

        reporte.append("=== CÁLCULO VLSM PARA RED BASE ").append(ipOrigen).append("/").append(prefijoBase).append(" ===\n\n");

        // 1. Regla de oro de VLSM: Ordenar hosts de mayor a menor
        hostsRequeridos.sort(Collections.reverseOrder());
        reporte.append("1. Hosts requeridos ordenados de mayor a menor:\n   ").append(hostsRequeridos).append("\n\n");

        long ipActual = ipAEntero(ipOrigen);
        int totalHostsSolicitados = 0;
        int totalHostsAsignados = 0;

        // Preparar la cabecera de la tabla
        tabla.append(String.format("%-10s | %-12s | %-18s | %-15s | %-15s | %-15s | %-15s\n",
                "Subred", "Hosts Útiles", "IP de red", "Máscara", "Primer Host", "Último Host", "Broadcast"));
        tabla.append("-".repeat(115)).append("\n"); // Línea separadora (requiere Java 11+)

        desglose.append("3. Desglose matemático paso a paso:\n\n");

        for (int i = 0; i < hostsRequeridos.size(); i++) {
            int hosts = hostsRequeridos.get(i);
            totalHostsSolicitados += hosts;

            // Encontrar la cantidad de bits de host (n) tal que (2^n - 2) >= hosts
            int bitsHost = 0;
            while ((Math.pow(2, bitsHost) - 2) < hosts) {
                bitsHost++;
            }

            int nuevoPrefijo = 32 - bitsHost;
            int hostsAsignables = (int) (Math.pow(2, bitsHost) - 2);
            totalHostsAsignados += hostsAsignables;

            long mascara = (0xFFFFFFFFL << bitsHost) & 0xFFFFFFFFL;
            long red = ipActual & mascara;
            long broadcast = red | (~mascara & 0xFFFFFFFFL);
            long primeraIp = red + 1;
            long ultimaIp = broadcast - 1;

            // Convertir todo a String para imprimir fácil
            String strRed = enteroAIp(red);
            String strMascara = enteroAIp(mascara);
            String strPrimeraIp = enteroAIp(primeraIp);
            String strUltimaIp = enteroAIp(ultimaIp);
            String strBroadcast = enteroAIp(broadcast);

            // Agregar fila a la tabla
            tabla.append(String.format("%-10s | %-12d | %-18s | %-15s | %-15s | %-15s | %-15s\n",
                    "Subred " + (i + 1), hostsAsignables, strRed + " /" + nuevoPrefijo, strMascara, strPrimeraIp, strUltimaIp, strBroadcast));

            // Agregar información al desglose
            desglose.append("➤ SUBRED ").append(i + 1).append(" (Petición: ").append(hosts).append(" hosts)\n");
            desglose.append("  Fórmula: 2^").append(bitsHost).append(" - 2 = ").append(hostsAsignables).append(" hosts utilizables.\n");
            desglose.append("  Nuevo Prefijo: /").append(nuevoPrefijo).append("\n");
            desglose.append("  Máscara Subred: ").append(strMascara).append("\n");
            desglose.append("  Dirección de Red: ").append(strRed).append("\n");
            desglose.append("  Primera IP Útil: ").append(strPrimeraIp).append("\n");
            desglose.append("  Última IP Útil: ").append(strUltimaIp).append("\n");
            desglose.append("  Broadcast: ").append(strBroadcast).append("\n");
            desglose.append("  [Desperdicio de IPs: ").append(hostsAsignables - hosts).append("]\n");
            desglose.append("--------------------------------------------------\n");

            // Preparar el salto para la siguiente subred
            ipActual = broadcast + 1;
        }

        // Ensamblar todo el reporte
        reporte.append("2. Tabla Resumen de Subredes:\n");
        reporte.append(tabla).append("\n");
        reporte.append(desglose);

        // --- SECCIÓN DE INTERPRETACIÓN ---
        reporte.append("=== RESUMEN E INTERPRETACIÓN ===\n");
        reporte.append("Total de hosts solicitados: ").append(totalHostsSolicitados).append("\n");
        reporte.append("Total de direcciones útiles asignadas: ").append(totalHostsAsignados).append("\n");

        long hostsRedBase = (long) (Math.pow(2, 32 - prefijoBase) - 2);
        reporte.append("Capacidad total de la red base ( /").append(prefijoBase).append(" ): ").append(hostsRedBase).append(" hosts.\n\n");

        reporte.append("INTERPRETACIÓN:\n");
        reporte.append("El algoritmo VLSM procesó las solicitudes exitosamente. Al asignar máscaras de longitud variable,\n");
        reporte.append("se ajustó el tamaño de cada subred a la demanda exacta, optimizando el espacio de direcciones y\n");
        reporte.append("evitando el desperdicio masivo que hubiera ocurrido con un esquema tradicional de enrutamiento con clase (FLSM).\n");

        return reporte.toString();
    }
}
