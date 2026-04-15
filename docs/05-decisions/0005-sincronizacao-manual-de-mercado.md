# Sincronização manual de mercado

## Status
- accepted

## Contexto
O módulo `market` precisava deixar de depender apenas de cadastro manual e começar a operar com dados externos reais de mercado e câmbio.

Ao mesmo tempo, o projeto ainda não estava pronto para introduzir scheduler, cache mais sofisticado, fallback complexo ou automação de sincronização.

## Decisão
A sincronização de dados de mercado será inicialmente manual e acionada por endpoints administrativos protegidos.

O fluxo adotado é:

- um endpoint administrativo dispara a sincronização;
- um client externo consulta a fonte;
- o dado externo é validado e normalizado;
- um novo registro histórico é persistido;
- o `pricing` continua lendo apenas o banco.

## Justificativa
Essa decisão foi tomada para:

- reduzir complexidade da fase inicial da integração;
- manter previsibilidade e testabilidade da solução;
- evitar acoplamento do pricing à disponibilidade da API externa;
- preservar o banco como fonte principal da verdade.

## Consequências

### Positivas
- fluxo simples e controlado;
- persistência histórica preservada;
- pricing desacoplado das APIs externas;
- integração mais fácil de testar e evoluir.

### Negativas ou trade-offs
- atualização não é automática;
- o sistema depende de ação explícita para sincronização;
- futuras evoluções podem exigir scheduler e políticas de atualização.

## Observações
Essa decisão é compatível com a fase atual do projeto e pode evoluir depois para automação, sem alterar o papel do `market` como repositório de histórico.
