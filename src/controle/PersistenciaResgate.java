package controle;

import enums.NivelRisco;
import enums.PrioridadeMissao;
import enums.StatusMissao;
import enums.StatusRobo;
import enums.TipoMissao;
import enums.TipoTerreno;
import logica.AreaOperacao;
import logica.Drone;
import logica.Missao;
import logica.Robo;
import logica.RoboTerrestre;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class PersistenciaResgate {
    private static final Set<Class<?>> TIPOS_PERMITIDOS = Set.of(
            DadosResgate.class, Robo.class, Drone.class, RoboTerrestre.class,
            AreaOperacao.class, Missao.class, TipoTerreno.class, NivelRisco.class,
            TipoMissao.class, PrioridadeMissao.class, StatusMissao.class, StatusRobo.class,
            LinkedHashMap.class, HashMap.class, ArrayList.class, Map.Entry[].class, Object[].class,
            String.class, Integer.class, Number.class, Enum.class
    );

    private PersistenciaResgate() {
    }

    static DadosResgate carregar(Path arquivo) throws IOException {
        try (var fluxo = new BufferedInputStream(Files.newInputStream(arquivo));
             var entrada = new ObjectInputStream(fluxo)) {
            entrada.setObjectInputFilter(PersistenciaResgate::filtrar);
            if (entrada.readObject() instanceof DadosResgate dados) {
                return dados;
            }
            throw new IOException("Formato de dados inválido.");
        } catch (NoSuchFileException e) {
            return new DadosResgate();
        } catch (ClassNotFoundException e) {
            throw new IOException("Formato de dados incompatível.", e);
        }
    }

    static void salvar(Path arquivo, DadosResgate dados) throws IOException {
        Path pasta = arquivo.getParent();
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, "central-", ".tmp");

        try {
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(dados);
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    private static ObjectInputFilter.Status filtrar(ObjectInputFilter.FilterInfo info) {
        if (info.depth() > 32) {
            return ObjectInputFilter.Status.REJECTED;
        }
        Class<?> tipo = info.serialClass();
        if (tipo == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }
        return TIPOS_PERMITIDOS.contains(tipo)
                ? ObjectInputFilter.Status.ALLOWED
                : ObjectInputFilter.Status.REJECTED;
    }
}
