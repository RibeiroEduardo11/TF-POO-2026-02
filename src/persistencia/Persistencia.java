package persistencia;

import controle.CentralResgate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;

import java.util.Scanner;

public class Persistencia {

    private static final Path ARQUIVO =
            Paths.get("dados", "central-resgate.dat");

    private static final Path TEMPORARIO =
            Paths.get("dados", "central-resgate.tmp");

    public static CentralResgate carregar(Scanner scanner) {

        if (!Files.exists(ARQUIVO)) {
            System.out.println("Nenhum arquivo anterior encontrado.");
            System.out.println("Iniciando uma nova central.");

            return new CentralResgate("Central de Resgate", scanner);
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(new FileInputStream(ARQUIVO.toFile()))) {

            Object objeto = entrada.readObject();

            if (!(objeto instanceof CentralResgate)) {
                throw new IOException("Arquivo de dados invalido.");
            }

            CentralResgate central = (CentralResgate) objeto;
            central.configurarScanner(scanner);

            System.out.println("Dados anteriores recuperados com sucesso!");

            return central;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Erro ao recuperar os dados: " + e.getMessage(), e
            );
        }
    }

    public static void salvar(CentralResgate central) {

        try {
            Files.createDirectories(ARQUIVO.getParent());

            try (ObjectOutputStream saida =
                         new ObjectOutputStream(new FileOutputStream(TEMPORARIO.toFile()))) {

                saida.writeObject(central);
            }

            try {
                Files.move(
                        TEMPORARIO,
                        ARQUIVO,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        TEMPORARIO,
                        ARQUIVO,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao salvar os dados: " + e.getMessage(), e
            );
        }
    }
}