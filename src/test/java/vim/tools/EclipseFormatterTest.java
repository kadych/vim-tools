package vim.tools;

import java.math.BigInteger;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import java.util.function.*;
import org.junit.*;
import static org.junit.Assert.*;

public class EclipseFormatterTest {
    private Properties options;

    @Before
    public void before() {
        options = new Properties();
    }

    private Predicate<String> checkExtension(final String... exts) {
        return value -> Arrays.stream(exts).filter(ext -> value.endsWith(ext)).count() > 0;
    }

    @Test
    public void test01() {
        String[] args = new String[] {"build.gradle", "pom.xml", ".gitignore", "ecf.iml"};
        List<File> files =
            Arrays.stream(args).filter(checkExtension(".gradle", ".gitignore")).map(File::new)
                .filter(File::isFile).collect(Collectors.toList());
        assertEquals(2, files.size());
    }

    @Test
    public void test02() throws IOException {
        final File userProperties = EclipseFormatter.USER_PROPERTIES;
        options = EclipseFormatter.getUserOptions(options);
        assertTrue(!userProperties.isFile() || !options.isEmpty());
    }

    @Test
    public void test03() throws IOException {
        options = EclipseFormatter.getDefaultOptions(options);
        assertTrue(!options.isEmpty());
    }

    @Test
    public void test04() {
        final String[] args = new String[] {"one", "two", "three", "four", "five", "six"};
        assertEquals("twothree", Arrays.stream(args).filter(s -> s.startsWith("t"))
            .collect(Collectors.joining()));
        assertEquals("twothree", Arrays.stream(args).filter(s -> s.startsWith("t"))
            .reduce("", String::concat));
        assertEquals("four,five", Arrays.stream(args).filter(s -> s.startsWith("f"))
            .collect(Collectors.joining(",")));
        assertEquals("four,five", Arrays.stream(args).filter(s -> s.startsWith("f"))
            .reduce((a, b) -> a + "," + b).orElse(""));
    }

    @Test
    public void test05() {
        final String[] args = new String[] {"one", "two", "three"};
        assertEquals("one,two,three", String.join(",", args));
        assertEquals("one,two,three", String.join(",", Arrays.stream(args).collect(
            Collectors.toList())));
        assertEquals("one,two,three", Arrays.stream(args)
            .collect(Collectors.joining(",")));
    }

    @Test
    public void test06() {
        final String str = "w00g";
        assertEquals("00", str.chars().filter(Character::isDigit).mapToObj(
            ch -> String.valueOf((char) ch)).collect(Collectors.joining()));
    }

    class Person implements Comparable<Person> {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public int compareTo(Person a) {
            if (name == null && a.name != null)
                return -1;
            if (name != null && a.name == null)
                return 1;
            return name.compareTo(a.name);
            // return age - a.age;
        }

        @Override
        public String toString() {
            return String.format("%-4s %d", name, age);
        }
    }

    private List<Person> persons;
    private Comparator<Person> ageComparator;
    private Function<Person, String> byName;
    private Function<Person, Integer> byAge;

    @Before
    public void beforePersons() {
        persons =
            Arrays.asList(new Person("tom", 6), new Person("emma", 7), new Person("john",
                7), new Person("zoe", 5));
        ageComparator = (a, b) -> a.getAge() - b.getAge();
        byName = p -> p.getName();
        byAge = p -> p.getAge();
    }

    @Test
    public void test07() {
        List<Person> twoPersons =
            persons.stream().filter(p -> p.getAge() == 7).sorted().collect(
                Collectors.toList());
        assertEquals(2, twoPersons.size());
        assertEquals("emma,john", twoPersons.stream().map(p -> p.getName()).collect(
            Collectors.joining(",")));
    }

    @Test
    public void test08() {
        assertEquals(5, persons.stream().mapToInt(p -> p.getAge()).min().orElse(0));
        assertEquals(2, persons.stream().filter(p -> p.getAge() == 7).count());
    }

    @Test
    public void test09() {
        Person youngest = persons.stream().sorted(ageComparator).findFirst().orElse(null);
        assertNotNull(youngest);
        assertEquals(5, youngest.getAge());

        Person oldest =
            persons.stream().sorted(ageComparator.reversed()).findFirst().orElse(null);
        assertNotNull(oldest);
        assertEquals(7, oldest.getAge());
    }

    @Test
    public void test10() {
        Person firstName =
            persons.stream().sorted(Comparator.comparing(byName)).findFirst()
                .orElse(null);
        assertNotNull(firstName);
        assertEquals("emma", firstName.getName());

        Person minAge =
            persons.stream().sorted(Comparator.comparing(byAge)).findFirst().orElse(null);
        assertNotNull(minAge);
        assertEquals(5, minAge.getAge());

    }

    private static List<String> words;

    public static List<String> loadWords(InputStream is,
        UseInstance<Stream<String>, List<String>, IOException> block) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return block.apply(reader.lines());
        }
    }

    @BeforeClass
    public static void loadWords() throws IOException {
        // try (BufferedReader reader =
        //     new BufferedReader(new InputStreamReader(EclipseFormatter.class
        //         .getResourceAsStream("/sgb-words.txt")))) {
        //     words = reader.lines().collect(Collectors.toList());
        // }

        words =
            loadWords(EclipseFormatter.class.getResourceAsStream("/sgb-words.txt"), st -> st
                .collect(Collectors.toList()));
    }

    @Test
    public void test11() {
        assertEquals(5757, words.size());
    }

    private Predicate<String> startsWith(char ch) {
        return w -> {
            // System.out.println("StartsWith : " + w);
            return w.startsWith(String.valueOf(ch));
        };
    }

    private Predicate<String> endsWith(char ch) {
        return w -> {
            // System.out.println("EndsWith : " + w);
            return w.endsWith(String.valueOf(ch));
        };
    }

    private Predicate<String> thirdLetter(char ch) {
        return w -> {
            // System.out.println("ThirdLetter : " + w);
            return w.length() >= 3 && w.charAt(2) == ch;
        };
    }

    private Predicate<String> alwaysTrue() {
        return w -> {
            // System.out.println("AlwaysTrue");
            return true;
        };
    }

    private Predicate<String> combine(List<Predicate<String>> filters) {
        return filters.stream().reduce((filter, next) -> filter.and(next)).orElseGet(
            this::alwaysTrue);
        // .orElse(str -> true);
    }

    @Test
    public void test12() {
        assertEquals(296, words.stream().filter(startsWith('a')).count());
        final List<Predicate<String>> filters =
            Arrays.asList(startsWith('a'), endsWith('a'), thirdLetter('a'));
        final List<String> atoa =
            words.stream().filter(combine(filters)).map(this::toUpper).collect(
                Collectors.toList());
        assertEquals(2, atoa.size());
    }

    @Test
    public void test13() {
        Map<Integer, List<Person>> grouped =
            persons.stream().collect(Collectors.groupingBy(Person::getAge));
        assertNotNull(grouped);
        assertEquals(3, grouped.size());
        assertNotNull(grouped.get(7));
        assertEquals(2, grouped.get(7).size());
    }

    private final Predicate<Path> isFile = f -> !Files.isDirectory(f);
    private final Predicate<Path> isHead = f -> "HEAD".equals(f.getFileName().toString());

    @Test
    public void test14() throws IOException {
        List<Path> files =
            Files.list(Paths.get(".git")).filter(isFile).collect(Collectors.toList());
        assertNotNull(files);
        assertTrue(files.size() > 1);

        List<Path> heads =
            Files.list(Paths.get(".git")).filter(isHead).collect(Collectors.toList());
        assertNotNull(heads);
        assertEquals(1, heads.size());
    }

    public static class Asset {
        public enum AssetType {
            BOND, STOCK
        }
        private final AssetType type;
        private final int value;

        public Asset(AssetType type, int value) {
            this.type = type;
            this.value = value;
        }

        public AssetType getType() {
            return type;
        }

        public int getValue() {
            return value;
        }
    }

    private List<Asset> assets;

    @Before
    public void beforeAsset() {
        assets =
            Arrays.asList(new Asset(Asset.AssetType.BOND, 1000), new Asset(
                Asset.AssetType.STOCK, 2000), new Asset(Asset.AssetType.BOND, 3000),
                new Asset(Asset.AssetType.STOCK, 4000));
    }

    public int totalAssetValue(List<Asset> assets, Predicate<Asset> predicate) {
        return assets.stream().filter(predicate).mapToInt(Asset::getValue).sum();
    }

    private final Predicate<Asset> allAssets = asset -> true;
    private final Predicate<Asset> bondAssets =
        asset -> asset.getType() == Asset.AssetType.BOND;
    private final Predicate<Asset> stockAssets =
        asset -> asset.getType() == Asset.AssetType.STOCK;

    @Test
    public void test15() {
        assertEquals(10000, totalAssetValue(assets, allAssets));
        assertEquals(4000, totalAssetValue(assets, bondAssets));
        assertEquals(6000, totalAssetValue(assets, stockAssets));
    }

    public interface Fly {
        default String takeOff() {
            return "Fly::takeOff()";
        }

        default String cruise() {
            return "Fly::cruise()";
        }

        default String turn() {
            return "Fly::turn()";
        }
    }

    public interface FastFly extends Fly {
        default String takeOff() {
            return "FastFly::takeOff()";
        }
    }

    public interface Sail {
        default String cruise() {
            return "Sail::cruise()";
        }

        default String turn() {
            return "Sail::turn()";
        }
    }

    public static class Vehicle {
        public String turn() {
            return "Vehicle::turn()";
        }
    }

    public static class SeaPlane extends Vehicle implements FastFly, Sail {
        int altitude;

        public String cruise() {
            String result = "SeaPlane::cruise()";
            if (altitude > 0)
                result = result + FastFly.super.cruise();
            else
                result = result + Sail.super.cruise();
            return result;
        }
    }

    @Test
    public void test16() {
        SeaPlane plane = new SeaPlane();
        assertEquals("SeaPlane::cruise()Sail::cruise()", plane.cruise());
        plane.altitude = 100;
        assertEquals("SeaPlane::cruise()Fly::cruise()", plane.cruise());
        assertEquals("Vehicle::turn()", plane.turn());
    }

    public static class Mailer {
        private String to, from, subject, body;

        private Mailer() {
        }

        public static String send(Consumer<Mailer> binder) {
            final Mailer mailer = new Mailer();
            binder.accept(mailer);
            return String.format("%s,%s,%s,%s", mailer.from, mailer.to, mailer.subject,
                mailer.body);
        }

        public Mailer to(String to) {
            this.to = to;
            return this;
        }

        public Mailer from(String from) {
            this.from = from;
            return this;
        }

        public Mailer subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Mailer body(String body) {
            this.body = body;
            return this;
        }
    }

    @Test
    public void test17() {
        assertEquals("kad@rdw.ru,it@rdw.ru,Caption,Message body", Mailer
            .send(mailer -> mailer.from("kad@rdw.ru").to("it@rdw.ru").subject("Caption")
                .body("Message body")));
    }

    @Test
    public void test18() {
        assertEquals("null", String.format("%s", (String) null));
        assertEquals("null1", null + "1");
        assertEquals("1null", "1" + null);
        assertEquals("nullnull", (String) null + null);
    }

    private static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FunctionalInterface
    private interface UseInstance<T, R, E extends Throwable> {
        R apply(T t) throws E;
    }

    @Test
    public void test19() {
        assertEquals(2, Stream.of("build.gradle", "src").map(
            file -> getCanonicalPath(new File(file))).count());
    }

    public interface MyRunnable {
        void run() throws Throwable;
    }

    public static <E extends Throwable> Throwable assertThrows(Class<E> exceptionClass,
        MyRunnable block) {
        try {
            block.run();
        } catch (Throwable ex) {
            if (exceptionClass.isInstance(ex))
                return ex;
        }
        fail("Failed to throw expected exception");
        return null;
    }

    public static class RodCutterException extends Exception {
        private static final long serialVersionUID = -1L;
    }

    public static class RodCutter {
        private int delta;

        public void setDelta(int delta) {
            this.delta = delta;
        }

        public int cut() throws RodCutterException {
            if (delta == 0)
                throw new RodCutterException();
            return delta;
        }
    }

    @Test
    public void test20() throws RodCutterException {
        RodCutter cutter = new RodCutter();
        cutter.setDelta(100);
        assertEquals(100, cutter.cut());
        cutter.setDelta(0);
        assertThrows(RodCutterException.class, () -> cutter.cut());
    }

    public static class Heavy {
        public Heavy() {
            System.out.println("Heavy created");
        }

        @Override
        public String toString() {
            return "quite heavy";
        }
    }

    public static class HolderNaive {
        private Heavy heavy;

        public HolderNaive() {
            System.out.println("HolderNaive created");
        }

        public synchronized Heavy getHeavy() {
            if (heavy == null)
                heavy = new Heavy();
            return heavy;
        }
    }

    public static class Holder {
        private Supplier<Heavy> supplier = this::createSupplier;

        public Holder() {
            System.out.println("Holder created");
        }

        public Heavy getHeavy() {
            return supplier.get();
        }

        public synchronized Heavy createSupplier() {
            class HeavyFactory implements Supplier<Heavy> {
                private final Heavy heavy = new Heavy();

                public Heavy get() {
                    return heavy;
                }
            }
            if (!HeavyFactory.class.isInstance(supplier))
                supplier = new HeavyFactory();
            return supplier.get();
        }
    }

    @Test
    public void test21() {
        // Holder naive = new Holder();
        // System.out.println("deferring heavy creation");
        // System.out.println(naive.getHeavy());
        // System.out.println(naive.getHeavy());
        // Supplier<Heavy> supplier = Heavy::new;
    }

    private List<String> names = Arrays.asList("Brad", "Kate", "Kim", "Jack", "Joe",
        "Mike", "Susan", "George", "Robert", "Julia", "Parker", "Benson");

    private int length(String s) {
        // System.out.println("length : " + s);
        return s.length();
    }

    private String toUpper(String s) {
        // System.out.println("toUpper : " + s);
        return s.toUpperCase();
    }

    @Test
    public void test22() {
        Stream<String> st =
            names.stream().filter(name -> length(name) == 3).map(name -> toUpper(name));
        // System.out.println("before findFirst");
        assertEquals("KIM", st.findFirst().get());
        // System.out.println("result : " + s);
    }

    public boolean evaluate(int value) {
        System.out.println("Evaluate: " + value);
        return value > 100;
    }

    public void calc(boolean a, boolean b) {
        System.out.println("calc: " + (a && b));
    }

    public void calcLazy(Supplier<Boolean> a, Supplier<Boolean> b) {
        System.out.println("calcLazy: " + (a.get() && b.get()));
    }

    @Test
    public void test23() {
        // calc(evaluate(1), evaluate(2));
        // calcLazy(() -> evaluate(1), () -> evaluate(2));
    }

    @Test
    public void test24() {
        List<Integer> range =
            IntStream.rangeClosed(2, 7).filter(val -> val % 2 != 0).collect(
                ArrayList::new, ArrayList::add, ArrayList::addAll);
        assertEquals(Arrays.asList(3, 5, 7), range);
    }

    public boolean isPrime(int number) {
        return number > 1
            && IntStream.rangeClosed(2, (int) Math.sqrt(number)).noneMatch(
                divisor -> number % divisor == 0);
    }

    public int primeAfter(int number) {
        int nextNumber = number + 1;
        if (isPrime(nextNumber))
            return nextNumber;
        else
            return primeAfter(nextNumber);
    }

    public List<Integer> primes(int fromNumber, int count) {
        return Stream.iterate(primeAfter(fromNumber - 1), this::primeAfter).limit(count)
            .collect(Collectors.toList());
    }

    @Test
    public void test25() {
        assertTrue(isPrime(2));
        assertTrue(isPrime(23));
        assertTrue(isPrime(107));
        assertFalse(isPrime(24));
        assertFalse(isPrime(25));
    }

    @Test
    public void test26() {
        assertEquals(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29), primes(1, 10));
    }

    @FunctionalInterface
    public interface TailCall<T> {
        public TailCall<T> apply();

        public default boolean isComplete() {
            return false;
        }

        public default T result() {
            throw new RuntimeException("not implemented");
        }

        public default T invoke() {
            return Stream.iterate(this, TailCall::apply).filter(TailCall::isComplete)
                .findFirst().get().result();
        }
    }

    public class TailCalls {
        public <T> TailCall<T> call(TailCall<T> nextCall) {
            return nextCall;
        }

        public <T> TailCall<T> done(T value) {
            return new TailCall<T>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public T result() {
                    return value;
                }

                @Override
                public TailCall<T> apply() {
                    throw new RuntimeException("not implemented");
                }
            };
        }

        // calc factorial ---------------------------------

        private TailCall<Integer> factorialTailRec(int factorial, int number) {
            if (number == 1)
                return done(factorial);
            return call(() -> factorialTailRec(factorial * number, number - 1));
        }

        private TailCall<BigInteger> factorialTailRec(BigInteger factorial, int number) {
            if (number == 1)
                return done(factorial);
            return call(() -> factorialTailRec(factorial.multiply(BigInteger
                .valueOf(number)), number - 1));
        }

        public int factorial(int number) {
            return factorialTailRec(1, number).invoke();
        }

        public BigInteger factorialBig(int number) {
            return factorialTailRec(BigInteger.ONE, number).invoke();
        }

        // calc fibonacci number --------------------------

        private TailCall<Integer> fibonacciTailRec(int current, int next, int number) {
            if (number == 1)
                return done(current);
            return call(() -> fibonacciTailRec(next, current + next, number - 1));
        }

        private TailCall<BigInteger> fibonacciTailRec(BigInteger current,
            BigInteger next, int number) {
            if (number == 1)
                return done(current);
            return call(() -> fibonacciTailRec(next, current.add(next), number - 1));
        }

        public int fibonacci(int number) {
            return fibonacciTailRec(1, 1, number).invoke();
        }

        public BigInteger fibonacciBig(int number) {
            return fibonacciTailRec(BigInteger.ONE, BigInteger.ONE, number).invoke();
        }
    }

    @Test
    public void test27() {
        TailCalls calls = new TailCalls();

        assertEquals(120, calls.factorial(5));
        assertEquals(55, calls.fibonacci(10));

        assertNotNull(calls.factorialBig(20000));
        assertNotNull(calls.fibonacciBig(20000));
    }

    public class Memorized {
        public <T, R> R callMemorized(final BiFunction<Function<T, R>, T, R> func,
            final T input) {
            Function<T, R> memorized = new Function<T, R>() {
                private final Map<T, R> store = new HashMap<>();

                public R apply(final T input) {
                    return store.computeIfAbsent(input, key -> func.apply(this, key));
                }
            };
            return memorized.apply(input);
        }
    }

    public class RodCutterBasic {
        private final List<Integer> prices;
        private final Memorized memorized = new Memorized();

        public RodCutterBasic(final List<Integer> prices) {
            this.prices = prices;
        }

        public int maxProfit(final int rodLength) {
            BiFunction<Function<Integer, Integer>, Integer, Integer> compute =
                (func, length) -> {
                    int profit =
                        (length > 0 && length <= prices.size()) ? prices.get(length - 1)
                            : 0;
                    for (int i = 1; i < length; i++) {
                        int priceWhenCut = func.apply(i) + func.apply(i - 1);
                        if (profit < priceWhenCut)
                            profit = priceWhenCut;
                    }
                    return profit;
                };
            return memorized.callMemorized(compute, rodLength);
        }
    }

    @Test
    public void test28() {
        final List<Integer> prices = Arrays.asList(2, 1, 1, 2, 2, 2, 1, 8, 9, 15);
        final RodCutterBasic cutter = new RodCutterBasic(prices);
        assertEquals(10, cutter.maxProfit(5));
        // System.out.println(cutter.maxProfit(22));
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    }

    public boolean isUnix() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("nix") >= 0 || osName.indexOf("mac") >= 0;
    }

    public String getParamValue(final String param) {
        final Pattern pattern = Pattern.compile("=\\s*['\"]?([^'\"]+)['\"]?\\s*");
        Matcher matcher = pattern.matcher(param);
        if (!matcher.find())
            return param;
        return matcher.group(1);
    }

    @Test
    public void test29() throws IOException {
        final File userDir = new File(System.getProperty("user.dir"));
        String sessionName = userDir.getName();
        assertNotNull(sessionName);

        assertTrue(isWindows() != isUnix());
    }

    @Test
    public void test30() {
        assertEquals("~/.sessions",
            getParamValue("let g:session_directory='~/.sessions'"));
        assertEquals("~/.sessions", getParamValue("~/.sessions"));
    }
}
