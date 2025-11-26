/**
 * ============================================================================
 * VEICULOS.JS - Gerenciamento de Veículos
 * ============================================================================
 * 
 * Este arquivo contém todas as funções necessárias para gerenciar veículos:
 * - Criar tabela de veículos
 * - Cadastrar novos veículos
 * - Excluir veículos
 * - Carregar fabricantes e modelos para os formulários
 * 
 * Estrutura do objeto Veículo:
 * {
 *   id: number,
 *   fabricante: { id: number, nome: string },
 *   modelo: { id: number, nome: string },
 *   ano: number,
 *   placa: string,
 *   cor: string,
 *   valor: number
 * }
 * ============================================================================
 */

/**
 * Cria uma tabela HTML para exibir os veículos cadastrados
 * 
 * Esta função é específica para veículos e gera uma tabela com
 * colunas para Fabricante, Modelo, Ano, Preço e ação de Deletar
 * 
 * @param {Array} dados - Array de objetos com os dados dos veículos
 * @returns {HTMLTableElement} Elemento table com os dados dos veículos
 * 
 * @example
 * const veiculos = await getData("http://localhost:8080/api/veiculos");
 * const tabela = criarTabelaVeiculo(veiculos);
 * document.querySelector("#veiculos").appendChild(tabela);
 */
const criarTabelaVeiculo = function(dados) {
    dados = dados.content;
    const tabela = document.createElement("table");
    const thead = document.createElement("thead");
    const tbody = document.createElement("tbody");
    
    // Cria o título da tabela
    const trTitle = document.createElement("tr");
    const th = document.createElement("th");
    th.textContent = "Veículos";
    th.colSpan = 7;
    trTitle.appendChild(th);
    thead.appendChild(trTitle);

    // Define o cabeçalho da tabela
    const cabecalho = ["Fabricante", "Modelo", "Ano", "Placa", "Cor", "Preço", "Ações"];
    const tr = document.createElement("tr");
    cabecalho.forEach(function(campo) {
        const th = document.createElement("th");
        th.textContent = campo;
        tr.appendChild(th);
    });

    // Adiciona classe para estilizar a tabela
    tabela.classList.add("tabela-dados");

    thead.appendChild(tr);
    tabela.appendChild(thead);

    // Cria o corpo da tabela com os dados dos veículos
    dados.forEach(function(item) {
        const tr = document.createElement("tr");
        
        // Coluna: Fabricante
        const tdFabricante = document.createElement("td");
        tdFabricante.textContent = item.modelo.fabricante.nome
        tr.appendChild(tdFabricante);
        
        // Coluna: Modelo
        const tdModelo = document.createElement("td");
        tdModelo.textContent = item.modelo.nome;
        tr.appendChild(tdModelo);
        
        // Coluna: Ano
        const tdAno = document.createElement("td");
        tdAno.textContent = item.ano;
        tr.appendChild(tdAno);
        
        // Coluna: Placa
        const tdPlaca = document.createElement("td");
        tdPlaca.textContent = item.placa || "-";
        tr.appendChild(tdPlaca);
        
        // Coluna: Cor
        const tdCor = document.createElement("td");
        tdCor.textContent = item.cor || "-";
        tr.appendChild(tdCor);
        
        // Coluna: Preço formatado em Real brasileiro
        const tdPreco = document.createElement("td");
        tdPreco.textContent = formatarPreco(item.valor);
        tr.appendChild(tdPreco);

        // Coluna: Botão de deletar
        const tdDeletar = document.createElement("td");
        tdDeletar.innerHTML = '<button class="btn delete">Deletar</button>';
        tdDeletar.addEventListener("click", async function() {
            if (confirm("Tem certeza que deseja excluir este veículo?")) {
                const resultado = await setDelete(`http://localhost:8080/api/veiculos/${item.id}`);

                if (isSuccess(resultado)) {
                    this.parentElement.remove();
                    alert("Veículo excluído com sucesso!");
                } else {
                    mostrarErro(resultado);
                }
            }
        });
        
        tr.appendChild(tdDeletar);
        tbody.appendChild(tr);
    });
    
    tabela.appendChild(tbody);
    return tabela;
}

/**
 * Formata um valor numérico para o formato de moeda brasileira (R$)
 * 
 * @param {number} valor - Valor numérico a ser formatado
 * @returns {string} Valor formatado como moeda brasileira
 * 
 * @example
 * formatarPreco(45000); // Retorna: "R$ 45.000,00"
 */
const formatarPreco = function(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

/**
 * Carrega os fabricantes disponíveis no select do formulário de veículo
 * 
 * Esta função busca todos os fabricantes da API e preenche o
 * elemento select com as opções disponíveis
 * 
 * @returns {Promise<void>}
 * 
 * @example
 * await carregarFabricantesVeiculo();
 */
const carregarFabricantesVeiculo = async function() {
    const selectFabricante = document.getElementById("fabricante-veiculo");
    const selectModelo = document.getElementById("modelo-veiculo");
    
    setRemoverElementos("#fabricante-veiculo option");
    setRemoverElementos("#modelo-veiculo option");
    
    const dadosFabricantes = await getData("http://localhost:8080/api/fabricantes");
    
    // Adiciona opção padrão
    const optionPadrao = document.createElement("option");
    optionPadrao.value = "";
    optionPadrao.textContent = "Selecione um fabricante";
    selectFabricante.appendChild(optionPadrao);
    
    // Adiciona os fabricantes
    dadosFabricantes.forEach(function(fabricante) {
        const option = document.createElement("option");
        option.value = fabricante.id;
        option.textContent = fabricante.nome;
        selectFabricante.appendChild(option);
    });
    
    // Inicializa o select de modelo congelado
    const optionModeloPadrao = document.createElement("option");
    optionModeloPadrao.value = "";
    optionModeloPadrao.textContent = "Selecione um fabricante primeiro";
    selectModelo.appendChild(optionModeloPadrao);
    selectModelo.disabled = true; // Congela o select até selecionar um fabricante
}

/**
 * Carrega os modelos disponíveis no select baseado no fabricante selecionado
 * 
 * Esta função filtra os modelos de acordo com o fabricante escolhido
 * e preenche o elemento select de modelos
 * Congela o select se não houver fabricante selecionado ou se não houver modelos
 * 
 * @param {number} fabricanteId - ID do fabricante para filtrar os modelos
 * @returns {Promise<void>}
 * 
 * @example
 * await carregarModelosVeiculo(1); // Carrega modelos do fabricante com ID 1
 */
const carregarModelosVeiculo = async function(fabricanteId) {
    const selectModelo = document.getElementById("modelo-veiculo");
    setRemoverElementos("#modelo-veiculo option");
    
    // Se não selecionou fabricante, congela o select
    if (!fabricanteId) {
        const optionPadrao = document.createElement("option");
        optionPadrao.value = "";
        optionPadrao.textContent = "Selecione um fabricante primeiro";
        selectModelo.appendChild(optionPadrao);
        selectModelo.disabled = true; // Congela o select
        return;
    }
    
    // Busca e filtra os modelos
    const dadosModelos = await getData("http://localhost:8080/api/modelos");
    const modelosFiltrados = dadosModelos.filter(function(modelo) {
        return modelo.fabricante.id == fabricanteId;
    });
    
    // Se não existem modelos para o fabricante, congela o select
    if (modelosFiltrados.length === 0) {
        const optionSemModelo = document.createElement("option");
        optionSemModelo.value = "";
        optionSemModelo.textContent = "Nenhum modelo para esse fabricante";
        selectModelo.appendChild(optionSemModelo);
        selectModelo.disabled = true; // Congela o select
        return;
    }
    
    // Habilita o select e adiciona opção padrão
    selectModelo.disabled = false; // Descongela o select
    const optionPadrao = document.createElement("option");
    optionPadrao.value = "";
    optionPadrao.textContent = "Selecione um modelo";
    selectModelo.appendChild(optionPadrao);
    
    // Adiciona os modelos filtrados
    modelosFiltrados.forEach(function(modelo) {
        const option = document.createElement("option");
        option.value = modelo.id;
        option.textContent = modelo.nome;
        selectModelo.appendChild(option);
    });
}

/**
 * Atualiza a tabela de veículos na página
 * 
 * Remove a tabela existente e cria uma nova com os dados atualizados
 * 
 * @returns {Promise<void>}
 * 
 * @example
 * await atualizarTabelaVeiculos();
 */
const atualizarTabelaVeiculos = async function() {
    setRemoverElementos(".tabela-dados");
    document.querySelector("#veiculos").style.display = "block";
    const dadosVeiculos = await getData("http://localhost:8080/api/veiculos");
    document.querySelector("#veiculos").appendChild(criarTabelaVeiculo(dadosVeiculos));
}

/**
 * Valida o formato da placa do veículo
 * 
 * Aceita dois formatos (com ou sem hífen):
 * - Formato antigo: ABC1234 (3 letras, 4 números)
 * - Formato Mercosul: ABC1D23 (3 letras, 1 número, 1 letra, 2 números)
 * 
 * @param {string} placa - Placa do veículo a ser validada
 * @returns {Object} Objeto com { valido: boolean, mensagem: string }
 * 
 * @example
 * validarPlaca("ABC1234"); // { valido: true, mensagem: "" }
 * validarPlaca("ABC1D23"); // { valido: true, mensagem: "" }
 * validarPlaca("123ABCD"); // { valido: false, mensagem: "..." }
 */
const validarPlaca = function(placa) {
    // Remove espaços em branco e hífens
    placa = placa.trim().toUpperCase().replace(/-/g, '');
    
    // Formato antigo: ABC1234 (3 letras + 4 números)
    const padraoAntigo = /^[A-Z]{3}[0-9]{4}$/;
    
    // Formato Mercosul: ABC1D23 (3 letras, 1 número, 1 letra, 2 números)
    const padraoMercosul = /^[A-Z]{3}[0-9][A-Z][0-9]{2}$/;
    
    if (padraoAntigo.test(placa) || padraoMercosul.test(placa)) {
        return { valido: true, mensagem: "" };
    }
    
    return { 
        valido: false, 
        mensagem: "Placa inválida. Use o formato ABC1234 (antigo) ou ABC1D23 (Mercosul)." 
    };
}

/**
 * Valida os dados do formulário de veículo
 * 
 * Verifica se todos os campos obrigatórios foram preenchidos
 * e se os valores estão dentro dos limites esperados
 * 
 * @param {Object} veiculo - Objeto com os dados do veículo a ser validado
 * @returns {Object} Objeto com { valido: boolean, mensagem: string }
 * 
 * @example
 * const resultado = validarVeiculo({ fabricante: { id: 1 }, modelo: { id: 2 }, ano: 2023, preco: 50000 });
 * if (!resultado.valido) {
 *     alert(resultado.mensagem);
 * }
 */
const validarVeiculo = function(veiculo) {
    const anoAtual = new Date().getFullYear();
    const selectModelo = document.getElementById("modelo-veiculo");
    const selectFabricante = document.getElementById("fabricante-veiculo");
    
    // Verifica se selecionou fabricante
    if (!selectFabricante.value) {
        return { valido: false, mensagem: "Por favor, selecione um fabricante." };
    }
    
    // Verifica se o select de modelo está desabilitado
    if (selectModelo.disabled) {
        return { valido: false, mensagem: "Não há modelos disponíveis para o fabricante selecionado. Por favor, cadastre um modelo primeiro." };
    }
    
    if (!veiculo.modelo || !veiculo.modelo.id) {
        return { valido: false, mensagem: "Por favor, selecione um modelo." };
    }
    
    if (!veiculo.ano || veiculo.ano < 1900 || veiculo.ano > anoAtual + 1) {
        return { valido: false, mensagem: `O ano deve estar entre 1900 e ${anoAtual + 1}.` };
    }
    
    if (!veiculo.placa || veiculo.placa.trim() === "") {
        return { valido: false, mensagem: "Por favor, informe a placa do veículo." };
    }
    
    const validacaoPlaca = validarPlaca(veiculo.placa);
    if (!validacaoPlaca.valido) {
        return { valido: false, mensagem: validacaoPlaca.mensagem };
    }
    
    if (!veiculo.cor || veiculo.cor.trim() === "") {
        return { valido: false, mensagem: "Por favor, informe a cor do veículo." };
    }
    
    // Verifica valor (aceita tanto 'preco' quanto 'valor' por compatibilidade)
    const valorVeiculo = veiculo.valor || veiculo.preco;
    if (!valorVeiculo || valorVeiculo <= 0) {
        return { valido: false, mensagem: "O preço deve ser maior que zero." };
    }
    
    return { valido: true, mensagem: "" };
}

/**
 * Limpa todos os campos do formulário de veículo
 * 
 * @example
 * limparFormularioVeiculo();
 */
const limparFormularioVeiculo = function() {
    document.getElementById("fabricante-veiculo").value = "";
    document.getElementById("modelo-veiculo").value = "";
    document.getElementById("ano-veiculo").value = "";
    document.getElementById("placa-veiculo").value = "";
    document.getElementById("cor-veiculo").value = "";
    document.getElementById("preco-veiculo").value = "";
}

// ============================================================================
// INICIALIZAÇÃO DOS EVENTOS DE VEÍCULOS
// ============================================================================

/**
 * Inicializa todos os eventos de click relacionados a veículos
 * 
 * Esta função centraliza todos os event listeners dos botões e formulários
 * relacionados ao módulo de veículos, mantendo o código organizado
 * 
 * Eventos configurados:
 * - Click no botão "Veículos" do menu (bt-veiculos)
 * - Click no botão "Novo veículo" (novo-veiculo)
 * - Change no select de fabricante do formulário
 * - Input no campo de placa (formatação)
 * - Blur no campo de placa (verificação de duplicidade)
 * - Submit do formulário de veículo
 * 
 * @example
 * inicializarEventosVeiculos();
 */
const inicializarEventosVeiculos = function() {
    
    // ========================================================================
    // Evento: Click no menu "Veículos"
    // ========================================================================
    document.getElementById("bt-veiculos").addEventListener("click", async function(event) {
        setMostrarOcultarElemento(true, ".minha-section");
        setRemoverElementos(".tabela-dados");
        document.querySelector("#veiculos").style.display = "block";
        
        // Carrega e exibe a tabela de veículos
        const dadosVeiculos = await getData("http://localhost:8080/api/veiculos");
        if (dadosVeiculos.ok === false) {
            document.querySelector("#veiculos").innerHTML = "<p>Erro ao carregar dados dos veículos.</p>";
            document.querySelector("#veiculos").style.color = "red";
            return;
        }
        document.querySelector("#veiculos").appendChild(criarTabelaVeiculo(dadosVeiculos));
    });

    // ========================================================================
    // Evento: Click no botão "Novo veículo"
    // ========================================================================
    document.getElementById("novo-veiculo").addEventListener("click", async function(event) {
        setMostrarOcultarElemento(true, ".modal-content");
        
        // Carrega os fabricantes para o select de veículos
        await carregarFabricantesVeiculo();
        
        MODAL.style.display = "block";
        setMostrarOcultarElemento(false, ".modal-content-veiculo");
    });

    // ========================================================================
    // Evento: Mudança no select de fabricante
    // ========================================================================
    document.getElementById("fabricante-veiculo").addEventListener("change", async function(event) {
        const fabricanteId = event.target.value;
        await carregarModelosVeiculo(fabricanteId);
    });

    // ========================================================================
    // Evento: Formatação automática da placa
    // ========================================================================
    document.getElementById("placa-veiculo").addEventListener("input", function(event) {
        let valor = event.target.value.toUpperCase();
        
        // Remove caracteres não permitidos (mantém apenas letras e números)
        valor = valor.replace(/[^A-Z0-9]/g, '');
        
        // Limita o tamanho em 7 caracteres (formato Mercosul ou antigo sem hífen)
        if (valor.length > 7) {
            valor = valor.substring(0, 7);
        }
        
        event.target.value = valor;
    });

    // ========================================================================
    // Evento: Verificação de placa duplicada ao sair do campo
    // ========================================================================
    document.getElementById("placa-veiculo").addEventListener("blur", async function(event) {
        const placa = event.target.value.trim().toUpperCase().replace(/-/g, '');
        
        // Só verifica se a placa tem formato válido
        if (placa.length >= 7) {
            const validacao = validarPlaca(placa);
            if (validacao.valido) {
                // Verifica se já existe
                const existe = await getData(`http://localhost:8080/api/veiculos/existe/${placa}`);
                if (existe) {
                    event.target.style.borderColor = "red";
                    event.target.style.backgroundColor = "#ffe6e6";
                    alert(`Atenção!\n\nA placa ${placa} já está cadastrada no sistema.\n\nPor favor, verifique se o veículo já existe antes de continuar.`);
                } else {
                    event.target.style.borderColor = "";
                    event.target.style.backgroundColor = "";
                }
            }
        }
    });

    // ========================================================================
    // Evento: Submissão do formulário de veículo
    // ========================================================================
    document.querySelector("#form-veiculo .botao-enviar").addEventListener("click", async function(event) {
        event.preventDefault();
        
        const modeloId = document.getElementById("modelo-veiculo").value;
        const ano = parseInt(document.getElementById("ano-veiculo").value);
        // Remove hífen e espaços, converte para maiúsculas
        const placa = document.getElementById("placa-veiculo").value.trim().toUpperCase().replace(/-/g, '');
        const cor = document.getElementById("cor-veiculo").value.trim();
        const valor = parseFloat(document.getElementById("preco-veiculo").value);
        
        // Cria o objeto veículo conforme o DTO do backend
        const novoVeiculo = {
            placa,
            cor,
            ano,
            valor,
            modelo: { id: modeloId }
        };
        
        // Valida os dados
        const validacao = validarVeiculo(novoVeiculo);
        if (!validacao.valido) {
            alert(validacao.mensagem);
            return;
        }
        
        // Envia para a API
        const resultado = await postData("http://localhost:8080/api/veiculos", novoVeiculo);
        
        if (isSuccess(resultado)) {
            alert("Veículo cadastrado com sucesso!");
            limparFormularioVeiculo();
            MODAL.style.display = "none";
            await atualizarTabelaVeiculos();
        } else {
            // Log do erro para debug
            console.error("Erro ao cadastrar veículo:", resultado);
            
            // Tratamento específico para conflito
            if (resultado.status === 409) {
                // Verifica se a mensagem menciona placa
                const mensagemBackend = resultado.message || "";
                if (mensagemBackend.toLowerCase().includes("placa")) {
                    alert(`⚠️ Placa Duplicada!\n\nA placa ${placa} já está cadastrada no sistema.\n\nVerifique se o veículo já existe ou se digitou a placa corretamente.`);
                } else {
                    // Pode ser outro tipo de conflito (ex: mesmo fabricante+modelo+ano)
                    alert(`⚠️ Registro Duplicado!\n\n${mensagemBackend}\n\nEste veículo (ou uma combinação similar) já existe no sistema.\n\nVerifique os dados informados.`);
                }
            } else {
                mostrarErro(resultado);
            }
        }
    });
    
}

// ============================================================================
// INICIALIZAÇÃO AUTOMÁTICA
// ============================================================================

// Executa a inicialização dos eventos quando o DOM estiver carregado
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializarEventosVeiculos);
} else {
    // DOM já está carregado
    inicializarEventosVeiculos();
}
