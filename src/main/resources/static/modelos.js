/*
    Função para criar uma tabela HTML para exibir os modelos de veículos
    geralmente implementado para tabelas específicas onde o cabeçalho é
    fixo e os dados podem ter estruturas diferentes
    Parâmetros:
    dados: array - array de objetos com os dados a serem exibidos na tabela
    Exemplo de uso:
    const tabelaModelos = criarTabelaModelo(dados);
*/
const criarTabelaModelo = function(dados) {
    const tabela = document.createElement("table");
    const thead = document.createElement("thead");
    const tbody = document.createElement("tbody");
    //cria o cabeçalho da tabela
    const trTitle = document.createElement("tr");
    const th = document.createElement("th");
    th.textContent = "Modelos";
    th.colSpan = 3;
    trTitle.appendChild(th);
    thead.appendChild(trTitle);

    const cabecalho = ["Modelo", "Fabricante", "País de Origem"];
    const tr = document.createElement("tr");
    cabecalho.forEach(function(campo) {
        const th = document.createElement("th");
        th.textContent = campo;
        tr.appendChild(th);
    });

    //adiciona classe para estilizar a tabela
    tabela.classList.add("tabela-dados");

    thead.appendChild(tr);
    tabela.appendChild(thead);

    //cria o corpo da tabela
    dados.forEach(function(item) {
        const tr = document.createElement("tr");
        //modelo
        const tdModelo = document.createElement("td");
        tdModelo.textContent = item.nome;
        tr.appendChild(tdModelo);
        //nome do fabricante
        const tdFabricante = document.createElement("td");
        tdFabricante.textContent = item.fabricante.nome;
        tr.appendChild(tdFabricante);

        //pais de origem
        const tdPaisOrigem = document.createElement("td");
        tdPaisOrigem.textContent = item.fabricante.paisOrigem;
        tr.appendChild(tdPaisOrigem);

        //icones
        const deletar = document.createElement("td");
        deletar.innerHTML = '<button class="btn delete">Deletar</button>';
        deletar.addEventListener("click", async function() {
           const resultado = await setDelete(`http://localhost:8080/api/modelos/${item.id}`);

           if (isSuccess(resultado)) {
               this.parentElement.remove();
               alert("Modelo excluído com sucesso!");
           } else {
               mostrarErro(resultado);
           }
        });
        
        tr.appendChild(deletar);

        tbody.appendChild(tr);
    });
    tabela.appendChild(tbody);

    return tabela;
}

/*implementação para salvar modelo*/
document.getElementById("salvar-modelo").addEventListener("click", async function(event) {
    event.preventDefault();
    const nome = document.getElementById("nome-modelo").value;
    const fabricanteId = document.getElementById("fabricante-modelo").value;
    const fabricanteNome = document.getElementById("fabricante-modelo").options[document.getElementById("fabricante-modelo").selectedIndex].text;
    const novoModelo = { nome: nome, fabricante: { id: fabricanteId, nome: fabricanteNome } };
    const resultado = await postData("http://localhost:8080/api/modelos", novoModelo);
    if (isSuccess(resultado)) {
        alert("Modelo salvo com sucesso!");
        document.getElementById("nome-modelo").value = "";
        document.getElementById("fabricante-modelo").value = "";
        MODAL.style.display = "none";
        //recriar a tabela de modelos
        setRemoverElementos(".tabela-dados");
        document.querySelector("#modelos").style.display = "block";
        const dadosModelos = await getData("http://localhost:8080/api/modelos");
        document.querySelector("#modelos").appendChild(criarTabelaModelo(dadosModelos));
    } else {
        mostrarErro(resultado);
    }
});