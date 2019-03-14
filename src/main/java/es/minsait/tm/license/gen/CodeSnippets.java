package es.minsait.tm.license.gen;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class CodeSnippets {
    // [0]: byte[]: content of license file xored with 0x55 or Exception
    // [1]: byte[]: public key
    // [2]: Long: next license check timestamp in ms (or null)
    // [3]: Integer: license check interval in ms
    // [4]: byte[]: path of last timestamp file - xored 0x55
    // [5]: byte[]: productID - xored 0x55
    // [6]: Integer: milliseconds to wait before exit in case if license verification fail
    // [7]: RuntimeException: throwable to throw when the license is not valid
    private static Object[] _INST_;


    public CodeSnippets() {
        _init_();
    }

    private synchronized void _init_() {
        // Set up parameters
        /*final Supplier<Object[]> stateProvider = () -> null; //_INST_;
        final Consumer<Object[]> stateConsumer = (newState) -> {
            //System.arraycopy(newState, 0, _INST_, 0, _INST_.length);
        };
        final Consumer<Exception> setException = (e) -> {
            final Object[] objects = stateProvider.get();
            objects[0] = e;
            stateConsumer.accept(objects);
        };
        final Object[] state = stateProvider.get();
        final Object oldState0 = state[0];*/

        final Object[] state = _INST_;
        final Object oldState0 = state[0];

        if (state[0] == null) {
            /*
            Read license.key file
            */
            // TODO: encode text
            final String $lp = "license.key";
            InputStream r = null;
            try {
                if (System.getProperty($lp) != null) {
                    // Defined system property license.key
                    r = Files.newInputStream(Paths.get(System.getProperty($lp)));
                } else {
                    // Try to get license.key from classpath root
                    r = this.getClass().getResourceAsStream($lp);
                    if (r == null) {
                        // Get license.key from current directory
                        // TODO: encode text
                        r = Files.newInputStream(Paths.get(System.getProperty("user.dir"), $lp));
                    }
                }

                ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = r.read(data, 0, data.length)) != -1) {
                    bufOut.write(data, 0, nRead);
                }
                final byte[] buf = bufOut.toByteArray();
                for (int i = 0; i < buf.length; i++) {
                    buf[i] = (byte)((buf[i] ^ 0x55) & 0xff);
                }
                state[0] = buf;
            } catch (Exception e) {
                state[0] = e;
            } finally {
                if (r != null) try { r.close(); } catch (Exception e) {/* ignore */}
            }
            //stateConsumer.accept(state);
        }

        /*
        Exit if check interval was not completed
         */
        if (state[2] != null && (Long) state[2] > System.nanoTime())
            return;



        if (!(state[0] instanceof Exception)) {

            // Checking license...

            try {
                final Properties license = new Properties();
                {
                    final byte[] buf = Arrays.copyOf((byte[]) state[0], ((byte[]) state[0]).length);
                    for (int i = 0; i < buf.length; i++) {
                        buf[i] = (byte) ((buf[i] ^ 0x55) & 0xff);
                    }
                    license.load(new ByteArrayInputStream(buf));
                }

                // verifying signature
                boolean signatureValid;
                {
                    // TODO: encode text
                    final String $s = "signature";
                    final byte[] sig = Base64.getDecoder().decode(license.getProperty($s));
                    final byte[] data;
                    {
                        /*license.entrySet().stream()
                            .filter(e -> !$s.equals(e.getKey()))
                            .sorted(Comparator.comparing(e -> e.getKey().toString()))
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("\n"))
                            .getBytes(StandardCharsets.UTF_8);*/
                        final ArrayList<String> props = new ArrayList<>();
                        for (Map.Entry<Object, Object> entry : new TreeMap<>(license).entrySet()) {
                            if ($s.equals(entry.getKey())) continue;
                            props.add(entry.getKey() + "=" + entry.getValue());
                        }
                        data = String.join("\n", props).getBytes(StandardCharsets.UTF_8);
                    }

                    try {
                        // TODO: encode text
                        Signature ts = Signature.getInstance("SHA1WithRSA");
                        ts.initVerify(KeyFactory.getInstance(new String(new char[]{'R', 'S', 'A'}))
                                .generatePublic(new X509EncodedKeySpec((byte[]) state[1]))
                        );
                        ts.update(data);
                        signatureValid = ts.verify(sig);
                    } catch (Exception e) {
                        throw new IllegalStateException();
                    }
                }

                // verifying product
                boolean productValid;
                {
                    final String $pi = "productId";
                    final byte[] array = (byte[]) state[5];
                    final byte[] buf = Arrays.copyOf(array, array.length);
                    for (int i = 0; i < buf.length; i++) {
                        buf[i] = (byte) ((buf[i] ^ 0x55) & 0xff);
                    }
                    productValid = new String(buf, StandardCharsets.UTF_8).equals(license.getProperty($pi));
                }

                // verifying timestamp
                boolean timestampValid;
                Instant timestamp;
                final Path tsFile;
                {
                    // TODO: encode text
                    final String $uh = "user.home";
                    final String s;
                    {
                        final byte[] array = (byte[]) state[4];
                        final byte[] buf = Arrays.copyOf(array, array.length);
                        for (int i = 0; i < buf.length; i++) {
                            buf[i] = (byte) ((buf[i] ^ 0x55) & 0xff);
                        }
                        s = new String(buf, StandardCharsets.UTF_8);
                    }
                    tsFile = Paths.get(System.getProperty($uh), s);
                    try {
                        timestamp = Files.getLastModifiedTime(tsFile).toInstant();
                    } catch (Exception ex) {
                        timestamp = Instant.now();
                    }
                    final LocalDate ld = timestamp.atZone(ZoneId.systemDefault()).toLocalDate();

                    // TODO: encode text
                    try {
                        final String $vf = "validFrom";
                        final String $vt = "validUntil";
                        timestampValid = !ld.isBefore(LocalDate.parse(license.getProperty($vf)))
                                && !ld.isAfter(LocalDate.parse(license.getProperty($vt)));
                    } catch (Exception e) {
                        throw new IllegalStateException();
                    }
                }

                final List<Boolean> results = Arrays.asList(signatureValid, productValid, timestampValid);
                for (int i1 = 0; i1 < results.size(); i1++) {
                    if (!results.get(i1)) {
                        state[0] = new RuntimeException("" + i1);
                        break;
                    }
                }

                // update timestamp
                if (Instant.now().isAfter(timestamp)) {
                    try (BufferedWriter o = Files.newBufferedWriter(tsFile)) {
                        o.append(UUID.randomUUID().toString());
                    } catch (IOException e) {
                         /* ignore */
                    }
                }
                state[2] = System.nanoTime() + (Integer) state[3] * 1000000;
            } catch (Exception e) {
                state[0] = e;
            }
        }

        if ((state[0] instanceof Exception) && !(oldState0 instanceof Exception)) {
            // exit in new thread with delay
            //System.out.println(state[0]);
            // TODO: encode
            try {
                Files.write(Paths.get(System.getProperty("user.dir"), "licence.fail"),
                        state[0].toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) { /* ignore */ }
            try {
                //System.exit(100);
                // TODO: encode
                final Method exit = Class.forName("java.lang.System").getMethod("exit", Integer.TYPE);
                exit.invoke(null, 100);
            } catch (Exception ex) {/*ignore*/}
            // finally throw exception with the wrong stack trace
            throw (RuntimeException) state[7];
        }
    }
}
