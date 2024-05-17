package net.so_default.so_78476856;

import net.so_default.so_78476856.SO78476856.Token;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TokenRepositoryAdapter {

  Mono<Token> findByGatewayToken(final String gatewayToken) {
    return Mono.just(new Token(gatewayToken));
  }
}
