package net.so_default.so_78478185;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SO78478185 {

  @GetMapping(path = "78478185")
  public String doSomething(String value) {

    var ids = Arrays.asList("val1", "val2", "val3");

    var rootWithChainsAndRoots = ids.stream()
                                    .reduce(new RootWithChainsAndRoots(new Root(value, "a level"), new ArrayList<>(), new ArrayList<>()),
                                            (reduction, s) -> {
                                              RootBuilder builder = new RootBuilder();
                                              builder.add(value, s);
                                              Root newRoot = builder.build();
                                              reduction.roots().add(newRoot);

                                              ChainBuilder chainBuilder = new ChainBuilder();
                                              reduction.chains().add(chainBuilder.build(newRoot));
                                              return reduction.withRoot(newRoot);
                                            },
                                            (reduction, s) -> s);

    var signature = signingServiceSign(rootWithChainsAndRoots.root()
                                                             .value(),
                                       rootWithChainsAndRoots.root()
                                                             .level()); // Blocking, needs to connect to a server through TCP. Server needs a fix before webClient can be used.
    var factory = new SignatureFactory();

    var composedSignature = IntStream.range(rootWithChainsAndRoots.chains().size() - 1, 0)
                                     .mapToObj(index -> new ChainAndRoot(rootWithChainsAndRoots.chains().get(index), rootWithChainsAndRoots.roots().get(index)))
                                     .reduce(signature,
                                             (previous, chainAndRoot) -> factory.createSignature(previous,
                                                                                                 chainAndRoot.chain(),
                                                                                                 chainAndRoot.root().value()),
                                             (previous, next) -> next);

    return composedSignature.content();
  }

  private record RootWithChainsAndRoots(Root root, List<Chain> chains, List<Root> roots) {

    public RootWithChainsAndRoots withRoot(final Root newRoot) {
      return new RootWithChainsAndRoots(newRoot, this.chains(), this.roots());
    }
  }

  private record ChainAndRoot(Chain chain, Root root) {

  }

  private Signature signingServiceSign(final String value, final String level) {
    return new Signature("signature: %s\nlevel: %s".formatted(value, level));
  }

  private static class RootBuilder {

    private final List<String> content = new ArrayList<>();

    void add(final String value, final String id) {
      content.add("value: %s\nid: %s".formatted(value, id));
    }

    Root build() {
      return new Root(content.toString(), "a level");
    }
  }

  private static class ChainBuilder {

    Chain build(Root newRoot) {
      return new Chain(newRoot);
    }
  }

  private static class SignatureFactory {

    Signature createSignature(final Signature signature, final Chain chain, final String value) {
      return new Signature("signature: %s\nchain: %s\nvalue: %s".formatted(signature, chain, value));
    }
  }

  private record Root(String value, String level) {

  }

  private record Chain(Root newRoot) {

  }

  private record Signature(String content) {

  }
}
