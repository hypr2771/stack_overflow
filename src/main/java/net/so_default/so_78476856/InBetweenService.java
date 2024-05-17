package net.so_default.so_78476856;

import io.micrometer.core.instrument.MeterRegistry;
import net.so_default.so_78476856.SO78476856.NetworkTokenInformationResponse;
import net.so_default.so_78476856.SO78476856.TokenizationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@Service
public class InBetweenService {

  private final TokenRepositoryAdapter repository;
  private final MeterRegistry          registry;

  public InBetweenService(TokenRepositoryAdapter repository, MeterRegistry registry) {
    this.repository = repository;
    this.registry   = registry;
  }

  @GetMapping(path = "78476856")
  public Mono<NetworkTokenInformationResponse> execute(@NonNull String gatewayToken) {
    System.out.println(STR."get token from database token \{gatewayToken}");
    return repository.findByGatewayToken(gatewayToken)
                     .switchIfEmpty(Mono.error(new TokenizationNotFoundException()))
                     .flatMap(token -> Mono.just(token.networkTokenInformationResponseExtracted()))
                     .doOnSuccess(_ -> registry.counter("getToken.controller", "gatewayToken", gatewayToken).increment());
  }

}
