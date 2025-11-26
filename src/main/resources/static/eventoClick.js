const MODAL = document.getElementById("modal");
const CLOSE_MODAL_BUTTON = document.getElementById("close-modal");

document.getElementById("bt-fabricantes").addEventListener("click", async function(event) {
    setMostrarOcultarElemento(true, ".minha-section");
    setRemoverElementos(".tabela-dados");
    document.querySelector("#fabricantes").style.display = "block";
    const dadosFabricantes = await getData("http://localhost:8080/api/fabricantes");
    document.querySelector("#fabricantes").appendChild(criarTabela(dadosFabricantes, "Fabricantes", "tabela-dados"));

});

CLOSE_MODAL_BUTTON.addEventListener("click", function() {
    MODAL.style.display = "none";
});

// Variável global para armazenar o ID do fabricante sendo editado
let fabricanteEmEdicao = null;

//evento de click para novo fabricante
document.getElementById("novo-fabricante").addEventListener("click", async function(event) {
    fabricanteEmEdicao = null; // Limpa o modo de edição
    setMostrarOcultarElemento(true, ".modal-content");
   
    // Atualiza o título do modal
    document.getElementById("modal-title").textContent = "Cadastro de Fabricante";
    
    //carregar json com nomes de paises do arquivo externo json
    const dadosPaises = await getData("http://localhost:8080/paises.json");
    const selectPais = document.getElementById("pais-fabricante");
    setRemoverElementos("#pais-fabricante option");
    dadosPaises.forEach(function(pais) {
        const option = document.createElement("option");
        option.value = pais.nome_pais;
        option.textContent = pais.nome_pais;
        selectPais.appendChild(option);
    });
    
    // Limpa os campos do formulário
    document.getElementById("nome-fabricante").value = "";
    document.getElementById("pais-fabricante").value = "";

    MODAL.style.display = "block";
    setMostrarOcultarElemento(false, ".modal-content-fabricante");
});

// Função para abrir o modal de edição de fabricante
async function abrirModalEdicaoFabricante(fabricante) {
    fabricanteEmEdicao = fabricante.id; // Armazena o ID do fabricante sendo editado
    setMostrarOcultarElemento(true, ".modal-content");
    
    // Atualiza o título do modal
    document.getElementById("modal-title").textContent = "Editar Fabricante";
    
    //carregar json com nomes de paises do arquivo externo json
    const dadosPaises = await getData("http://localhost:8080/paises.json");
    const selectPais = document.getElementById("pais-fabricante");
    setRemoverElementos("#pais-fabricante option");
    dadosPaises.forEach(function(pais) {
        const option = document.createElement("option");
        option.value = pais.nome_pais;
        option.textContent = pais.nome_pais;
        selectPais.appendChild(option);
    });
    
    // Preenche os campos com os dados do fabricante
    document.getElementById("nome-fabricante").value = fabricante.nome;
    document.getElementById("pais-fabricante").value = fabricante.paisOrigem;

    MODAL.style.display = "block";
    setMostrarOcultarElemento(false, ".modal-content-fabricante");
}

//evento de clic para salvar novo fabricante ou atualizar existente
document.getElementById("salvar-fabricante").addEventListener("click", async function(event) {
    event.preventDefault();
    const nome = document.getElementById("nome-fabricante").value;
    const paisOrigem = document.getElementById("pais-fabricante").value;
    const fabricanteData = { nome: nome, paisOrigem: paisOrigem };

    let resultado;
    if (fabricanteEmEdicao) {
        // Modo de edição - usa PUT
        resultado = await putData(`http://localhost:8080/api/fabricantes/${fabricanteEmEdicao}`, fabricanteData);
    } else {
        // Modo de cadastro - usa POST
        resultado = await postData("http://localhost:8080/api/fabricantes", fabricanteData);
    }
    
    if (isSuccess(resultado)) {
        const mensagem = fabricanteEmEdicao ? "Fabricante atualizado com sucesso!" : "Fabricante salvo com sucesso!";
        alert(mensagem);
        document.getElementById("nome-fabricante").value = "";
        document.getElementById("pais-fabricante").value = "";
        fabricanteEmEdicao = null; // Limpa o modo de edição
        MODAL.style.display = "none";

        //recriar a tabela de fabricantes
        setRemoverElementos(".tabela-dados");
        document.querySelector("#fabricantes").style.display = "block";
        const dadosFabricantes = await getData("http://localhost:8080/api/fabricantes");
        document.querySelector("#fabricantes").appendChild(criarTabela(dadosFabricantes, "Fabricantes", "tabela-dados"));

    } else {
        mostrarErro(resultado);
    }
});


//evento de click para novo modelo
document.getElementById("novo-modelo").addEventListener("click", async function(event) {
    setMostrarOcultarElemento(true, ".modal-content");
    //aqui vou fazer a carga dos fabricantes para o select
    const dadosFabricantes = await getData("http://localhost:8080/api/fabricantes");
    if(dadosFabricantes.status === 404 || dadosFabricantes.error) {
        alert("Erro ao carregar dados dos fabricantes. erro: " + dadosFabricantes.message);
        return;
    }
    setRemoverElementos("#fabricante-modelo option");

    document.getElementById("fabricante-modelo").appendChild(new Option("Selecione um fabricante", ""));
    dadosFabricantes.forEach(function(fabricante) {
        const option = document.createElement("option");
        option.value = fabricante.id;
        option.textContent = fabricante.nome;
        document.getElementById("fabricante-modelo").appendChild(option);
    });

    MODAL.style.display = "block";
    setMostrarOcultarElemento(false, ".modal-content-modelo");
});

document.getElementById("bt-modelos").addEventListener("click", async function(event) {
    setMostrarOcultarElemento(true, ".minha-section");
    setRemoverElementos(".tabela-dados");
    document.querySelector("#modelos").style.display = "block";
    const dadosModelo = await getData("http://localhost:8080/api/modelos");
    if (dadosModelo.ok === false) {
        document.querySelector("#modelos").innerHTML = "<p>Erro ao carregar dados dos modelos.</p>";
        document.querySelector("#modelos").style.color = "red";

        return;
    }
    document.querySelector("#modelos").appendChild(criarTabelaModelo(dadosModelo));

});