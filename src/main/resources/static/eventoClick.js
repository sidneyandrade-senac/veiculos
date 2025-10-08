document.getElementById("bt-fabricantes").addEventListener("click", async function(event) {
    setMostrarOcultarElemento(true, ".minha-section");
    setRemoverElementos(".tabela-dados");
    document.querySelector("#fabricantes").style.display = "block";
    const dadosFabricantes = await getData("http://localhost:8080/api/fabricantes");
    document.querySelector("#fabricantes").appendChild(criarTabela(dadosFabricantes, "Fabricantes", "tabela-dados"));

});

document.getElementById("bt-modelos").addEventListener("click", async function(event) {
    setMostrarOcultarElemento(true, ".minha-section");
    setRemoverElementos(".tabela-dados");
    document.querySelector("#modelos").style.display = "block";
    const dadosFabricantes = await getData("http://localhost:8080/api/modelos");
    document.querySelector("#modelos").appendChild(criarTabelaModelo(dadosFabricantes));

});

document.getElementById("bt-veiculos").addEventListener("click", function(event) {
    setMostrarOcultarElemento(true, ".minha-section");
    setRemoverElementos(".tabela-dados");
    document.querySelector("#veiculos").style.display = "block";

});