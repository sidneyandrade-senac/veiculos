# Documenta??o detalhada de `src/main/resources/static/veiculos.js`

## 1. Prop?sito do m?dulo

O arquivo `veiculos.js` concentra todas as a??es de front-end relacionadas ao ciclo de vida de ve?culos na aplica??o web. Ele ? respons?vel por carregar dados do backend, montar componentes de interface (tabela e formul?rios), validar entradas do usu?rio e orquestrar chamadas REST para cria??o e exclus?o de registros.

## 2. Depend?ncias externas

O script assume a exist?ncia dos seguintes utilit?rios globais, fornecidos por outros arquivos JavaScript do projeto:

- `getData(url)` ? Faz requisi??es HTTP GET e retorna JSON.
- `postData(url, payload)` ? Envia requisi??es HTTP POST com corpo JSON.
- `setDelete(url)` ? Envia requisi??es HTTP DELETE.
- `isSuccess(response)` ? Indica se a resposta HTTP representa sucesso.
- `mostrarErro(response)` ? Exibe mensagens de erro para o usu?rio.
- `setRemoverElementos(selector)` ? Remove elementos do DOM com base em um seletor CSS.
- `setMostrarOcultarElemento(show, selector)` ? Mostra ou oculta elementos.
- `MODAL` ? Refer?ncia ao elemento modal principal da aplica??o.

Sem esses utilit?rios o arquivo n?o funciona corretamente; verifique `static/comum.js` e correlatos para implementa??o.

## 3. Estrutura esperada dos dados

O backend exp?e recursos REST em `http://localhost:8080/api` e retorna objetos compat?veis com o DTO abaixo:

```javascript
{
  id: number,
  modelo: {
    id: number,
    nome: string,
    fabricante: {
      id: number,
      nome: string
    }
  },
  ano: number,
  placa: string,
  cor: string,
  valor: number
}
```

As respostas de listagem (`/api/veiculos`) retornam um objeto paginado que possui a propriedade `content`, contendo o array usado nas renderiza??es.

## 4. Constru??o da tabela de ve?culos

### Fun??o: `criarTabelaVeiculo(dados)`

- Entrada: objeto paginado com `content` (lista de ve?culos).
- Sa?da: elemento `<table>` preenchido.
- Passos principais:
  1. Cria `<table>` com cabe?alho personalizado.
  2. Para cada ve?culo, adiciona linha com fabricante, modelo, ano, placa, cor e valor (formatado).
  3. Anexa um bot?o "Deletar" que confirma a a??o, dispara `setDelete` e remove a linha DOM se a opera??o for bem-sucedida.

> **Importante:** `dados` deve trazer a chave `content`. Caso o backend mude a forma de pagina??o, ajuste essa fun??o ou normalize a resposta antes de chamar `criarTabelaVeiculo`.

### Fun??o auxiliar: `formatarPreco(valor)`

- Usa `Intl.NumberFormat` com locale `pt-BR` para exibir `valor` como moeda (ex.: `R$ 45.000,00`).
- Qualquer valor n?o num?rico produzir? `'NaN'` na tela. Certifique-se de validar no backend.

## 5. Carregamento de fabricantes e modelos

### `carregarFabricantesVeiculo()`

- Limpa as op??es dos selects `#fabricante-veiculo` e `#modelo-veiculo`.
- Busca os fabricantes via GET `/api/fabricantes`.
- Preenche o select com uma op??o padr?o e as op??es reais.
- Inicializa o select de modelos desabilitado, convidando o usu?rio a escolher um fabricante.

### `carregarModelosVeiculo(fabricanteId)`

- Requer um `fabricanteId` v?lido.
- Busca todos os modelos via GET `/api/modelos` e filtra pelo fabricante selecionado.
- Se existirem modelos, habilita o select e insere as op??es; caso contr?rio, mant?m o select desabilitado com uma mensagem explicativa.

> **Poss?vel evolu??o:** Expor endpoint `/api/fabricantes/{id}/modelos` para buscar j? filtrado e reduzir carga de dados.

## 6. Atualiza??o de tabela

### `atualizarTabelaVeiculos()`

- Remove tabelas renderizadas anteriormente (`.tabela-dados`).
- Garante a exibi??o da se??o `#veiculos`.
- Busca a lista atualizada via GET `/api/veiculos` e anexa o resultado de `criarTabelaVeiculo`.

Essa fun??o ? usada ap?s opera??es de cadastro e pode ser reutilizada em atualiza??es peri?dicas, caso necess?rio.

## 7. Valida??es e utilidades de formul?rio

### `validarPlaca(placa)`

- Normaliza a placa (mai?sculas, remove espa?os e h?fens).
- Aceita dois padr?es via regex: formato antigo `ABC1234` e padr?o Mercosul `ABC1D23`.
- Retorna `{valido: boolean, mensagem: string}` permitindo reuso em outras camadas.

### `validarVeiculo(veiculo)`

Abrange valida??es de integridade do formul?rio:

1. Verifica se os selects de fabricante e modelo est?o devidamente escolhidos/habilitados.
2. Garante que o ano esteja entre 1900 e o ano corrente + 1.
3. Exige placa, cor e valor positivos.
4. Reutiliza `validarPlaca` para validar o formato.

Retorna a mesma estrutura `{valido, mensagem}`, que ser? exibida via `alert` em caso de falha.

### `limparFormularioVeiculo()`

Zera os campos b?sicos do formul?rio. Ap?s salvar um ve?culo, essa fun??o ? chamada e o modal ? fechado.

## 8. Inicializa??o e eventos

### `inicializarEventosVeiculos()`

Centraliza todos os listeners associados ao m?dulo. Os elementos DOM acessados devem existir na p?gina:

- `#bt-veiculos` ? bot?o do menu que exibe a lista de ve?culos.
  - Oculta se??es gen?ricas, limpa tabela anterior, busca dados e renderiza nova tabela.
  - Em caso de falha HTTP, substitui o conte?do por mensagem de erro em vermelho.

- `#novo-veiculo` ? abre o modal de cadastro.
  - Mostra conte?do do modal, carrega fabricantes e finalmente exibe a se??o espec?fica do ve?culo.

- `#fabricante-veiculo` (`change`)
  - Recarrega o select de modelos com base no fabricante escolhido.

- `#placa-veiculo` (`input`)
  - Normaliza caracteres conforme usu?rio digita (apenas letras/n?meros, m?ximo 7 caracteres).

- `#placa-veiculo` (`blur`)
  - Ap?s sair do campo, verifica duplicidade consultando todos os ve?culos atuais e comparando placas.
  - Destaca o campo com borda vermelha/fundo rosa e exibe alerta caso a placa j? exista.

- `#form-veiculo .botao-enviar` (`click`)
  - Prev? envio via bot?o estilizado dentro do formul?rio.
  - Constr?i o objeto `novoVeiculo`, loga para depura??o, valida entradas e envia `POST /api/veiculos`.
  - Em sucesso: alerta, limpa formul?rio, fecha modal e chama `atualizarTabelaVeiculos()`.
  - Em erro `409`: mostra mensagens contextualizadas para placa duplicada ou outro conflito.
  - Em outros erros: delega para `mostrarErro()`.

### Auto-inicializa??o

No final do arquivo, h? um bloco que garante a execu??o de `inicializarEventosVeiculos()` assim que o DOM estiver pronto (ouvindo `DOMContentLoaded` ou executando imediatamente caso o documento j? esteja carregado). Isso assegura que os listeners sejam registrados mesmo quando o script ? carregado no rodap? da p?gina.

## 9. Fluxos principais

### 9.1. Exibir lista de ve?culos

1. Usu?rio clica em "Ve?culos" (`#bt-veiculos`).
2. Script limpa renderiza??es anteriores, chama GET `/api/veiculos`.
3. `criarTabelaVeiculo()` monta a tabela, cada linha inclui bot?o de exclus?o.
4. Usu?rio pode clicar em "Deletar"; ao confirmar, envia DELETE espec?fico e remove linha da tabela.

### 9.2. Cadastrar novo ve?culo

1. Usu?rio aciona "Novo ve?culo" (`#novo-veiculo`).
2. Modal aparece com selects vazios; fabricantes s?o carregados e modelos permanecem desabilitados at? sele??o.
3. Ao escolher um fabricante, modelos s?o filtrados dinamicamente.
4. Campo de placa sanitiza entrada em tempo real e checa duplicidade ao perder foco.
5. Ao clicar em "Salvar", dados s?o validados; se v?lidos, `postData` envia novo registro.
6. Em sucesso, modal fecha, tabela ? recarregada via `atualizarTabelaVeiculos()`.

## 10. Pontos de aten??o e recomenda??es

- **Pagina??o:** o script assume que `dados.content` cont?m **todos** os ve?culos necess?rios. Se a API come?ar a paginar, avalie adicionar par?metros de pagina??o ou ajustar a renderiza??o para lidar com m?ltiplas p?ginas.
- **Sincroniza??o de dados:** a verifica??o de placa duplicada (`blur`) depende de uma chamada GET completa. Em bases de dados extensas, considere endpoint espec?fico `/api/veiculos/placa/{placa}` ou valida??o direta no backend.
- **Controle de estado do modal:** `MODAL` ? tratado como global. Certifique-se de que o HTML define esse identificador e controle a anima??o/oculta??o de acordo com a experi?ncia de usu?rio desejada.
- **Internacionaliza??o:** mensagens de erro e alertas est?o em portugu?s e com emojis. Se precisar suportar m?ltiplos idiomas, centralize essas strings em um arquivo de mensagens.
- **Tratamento de n?meros:** `parseFloat` em `preco-veiculo` n?o gerencia separadores de milhar; garanta que o campo aceite apenas n?meros sem formata??o ou normalize a string antes de converter.

## 11. Extens?es poss?veis

1. **Edi??o de ve?culos:** adicionar bot?o "Editar" reutilizando o modal para alterar registros via PUT.
2. **Pagina??o e ordena??o:** integrar elementos de UI para navegar por grandes listas.
3. **Feedback n?o bloqueante:** substituir `alert` por componentes modais/toast para melhor UX.
4. **Testes automatizados:** criar testes de front-end (ex.: Jest + jsdom) para validar fun??es puras (`validarPlaca`, `validarVeiculo`).

## 12. Checklist r?pido para novos desenvolvedores

- Verifique se os IDs referenciados (`bt-veiculos`, `novo-veiculo`, `fabricante-veiculo`, etc.) existem no HTML.
- Confirme que os utilit?rios globais listados na se??o 2 est?o carregados antes de `veiculos.js`.
- Ajuste os endpoints se a API estiver hospedada em dom?nio/porta diferentes.
- Para depura??o, utilize os logs inseridos (ex.: `console.log("Enviando ve?culo:"... )`).
- Ap?s altera??es visuais, revise o CSS associado ? classe `.tabela-dados` em `static/styles.css`.
