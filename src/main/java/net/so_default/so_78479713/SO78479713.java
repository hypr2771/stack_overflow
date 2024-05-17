package net.so_default.so_78479713;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SO78479713 {

  @GetMapping(path = "/78479713")
  public Mono<SoloPreguntaImp> getPreguntas(@RequestParam String perfil) {
    return getIdEntrevista(perfil)
        .flatMap(idEntrevista -> getPreguntasFromEntrevista(idEntrevista.id()))
        .next();
  }

  public Flux<IdEntrevista> getIdEntrevista(String perfil) {
    return Flux.just(new IdEntrevista("E_1_%s".formatted(perfil)),
                     new IdEntrevista("E_2_%s".formatted(perfil)),
                     new IdEntrevista("E_3_%s".formatted(perfil)),
                     new IdEntrevista("E_4_%s".formatted(perfil)),
                     new IdEntrevista("E_5_%s".formatted(perfil)),
                     new IdEntrevista("E_6_%s".formatted(perfil)),
                     new IdEntrevista("E_7_%s".formatted(perfil)));
  }

  public Flux<SoloPreguntaImp> getPreguntasFromEntrevista(String entrevistaId) {
    return switch (entrevistaId) {
      case "E_5_test" -> Flux.just(new SoloPreguntaImp("FOUND"));
      case "E_6_test" -> Flux.just(new SoloPreguntaImp("SHOULD NOT BE RETURNED"));
      default -> Flux.empty();
    };
  }

  record SoloPreguntaImp(String id) {

  }

  record IdEntrevista(String id) {

  }

}
