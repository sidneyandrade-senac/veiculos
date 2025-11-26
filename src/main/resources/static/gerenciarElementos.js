/*
FunÃƒÂ§ÃƒÂ£o para criar uma tabela HTML a partir de um array de objetos
geralmente implementado para tabelas genericas onde o cabeçalho ÃƒÂ©
criado dinamicamente a partir das chaves dos objetos do array
ParÃƒÂ¢metros:
dados: array - array de objetos com os dados a serem exibidos na tabela
Exemplo de uso:
const tabela = criarTabela(dados);
*/
const criarTabela = function(dados, titulo = "Tabela", classe) {
    const tabela = document.createElement("table");
    const thead = document.createElement("thead");
    const tbody = document.createElement("tbody");

    //cria o cabeÃƒÆ’Ã‚Â§alho da tabela
    const cabecalho = Object.keys(dados[0]);

    const trTitle = document.createElement("tr");
    const th = document.createElement("th");
    th.textContent = titulo;
    th.colSpan = cabecalho.length + 1;
    trTitle.appendChild(th);
    thead.appendChild(trTitle);

    const tr = document.createElement("tr");

    cabecalho.forEach(function(campo) {
        const th = document.createElement("th");
        th.textContent = campo;
        tr.appendChild(th);
    });

    //adiciona classe para estilizar a tabela
    tabela.classList.add(classe);

    thead.appendChild(tr);
    tabela.appendChild(thead);

    //cria o corpo da tabela
    dados.forEach(function(item) {
        const tr = document.createElement("tr");
        cabecalho.forEach(function(campo) {
            const td = document.createElement("td");
            td.textContent = item[campo];
            tr.appendChild(td);
        });
        
        // Coluna de Ações com botões Editar e Excluir
        const tdAcoes = document.createElement("td");
        tdAcoes.style.display = "flex";
        tdAcoes.style.gap = "5px";
        
        // Botão Editar
        const btnEditar = document.createElement("button");
        btnEditar.textContent = "Editar";
        btnEditar.classList.add("btn", "edit");
        btnEditar.style.cursor = "pointer";
        btnEditar.addEventListener("click", async function(event) {
            await abrirModalEdicaoFabricante(item);
        });
        
        // Botão Excluir
        const btnExcluir = document.createElement("button");
        btnExcluir.textContent = "Excluir";
        btnExcluir.classList.add("btn", "delete");
        btnExcluir.style.cursor = "pointer";
        btnExcluir.addEventListener("click", async function(event) {
            if(confirm("Tem certeza que deseja excluir este item?")) {
                const resposta = await setDelete(`http://localhost:8080/api/fabricantes/${item.id}`);
                if(resposta.success) {
                    tr.remove();
                    alert(resposta.message);
                } else {
                    mostrarErro(resposta);
                }
            }
        });

        tdAcoes.appendChild(btnEditar);
        tdAcoes.appendChild(btnExcluir);
        tr.appendChild(tdAcoes);
        tbody.appendChild(tr);
    });
    tabela.appendChild(tbody);

    return tabela;
}

/*
FunÃƒÂ§ÃƒÂ£o para mostrar ou ocultar elementos
ParÃƒÂ¢metros:
esconder: booleano - true para ocultar, false para mostrar
elemento: string - classe ou id do elemento a ser manipulado
Exemplo de uso:
setMostrarOcultarElemento(true, ".minha-section");
*/
const setMostrarOcultarElemento = function(esconder, elemento) {
    document.querySelectorAll(elemento).forEach(function(section) {
        section.style.display = esconder ? "none" : "block";
    });
}

/** Remove elements from the DOM
 * ParÃƒÂ¢metros:
 * seletor: string - class or id of the element to be removed
 * Exemplo de uso:
 * setRemoverElementos(".minha-section table");
 */
const setRemoverElementos = function(seletor) {
    document.querySelectorAll(seletor).forEach(function(elemento) {
        elemento.remove();
    });
}
