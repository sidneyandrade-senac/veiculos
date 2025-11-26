    // Função para requisições GET
    async function getData(url) {
        try {
            const response = await fetch(url);
            if (response.ok) {
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    return await response.json();
                } else {
                    return await response.text();
                }
            } else {
                try {
                    const error = await response.json();
                    return { error: true, status: response.status, ...error };
                } catch {
                    return { error: true, status: response.status, message: response.statusText };
                }
            }
        } catch (error) {
            return { error: true, message: "Erro de conexão: " + error.message };
        }
    }

    // Função para requisições POST
    async function postData(url, data) {
        try {
            const response = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    return await response.json();
                } else {
                    return await response.text();
                }
            } else {
                try {
                    const error = await response.json();
                    return { error: true, status: response.status, ...error };
                } catch {
                    return { error: true, status: response.status, message: response.statusText };
                }
            }
        } catch (error) {
            return { error: true, message: "Erro de conexão: " + error.message };
        }
    }

    // Função para requisições PUT
    async function putData(url, data) {
        try {
            const response = await fetch(url, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });     
            if (response.ok) {
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    return await response.json();
                } else {
                    return await response.text();
                }
            } else {
                try {
                    const error = await response.json();
                    return { error: true, status: response.status, ...error };
                } catch {
                    return { error: true, status: response.status, message: response.statusText };
                }
            }
        } catch (error) {
            return { error: true, message: "Erro de conexão: " + error.message };
        }
    }

    // Função para requisições DELETE
    async function setDelete(url) {
        try {
            const response = await fetch(url, { method: "DELETE" });
            
            if (response.ok) {
                return { success: true, message: "Excluído com sucesso" };
            } else {
                try {
                    //erro do exception
                    const error = await response.json();
                    return { error: true, status: response.status, ...error };
                } catch {
                    return { error: true, status: response.status, message: response.statusText };
                }
            }
        } catch (error) {
            return { error: true, message: "Erro de conexão: " + error.message };
        }
    }

    // Verifica se a resposta é sucesso
    function isSuccess(response) {
        return response && !response.error;
    }

    // Mostra erro específico do backend
    function mostrarErro(response) {
        if (response.message) {
            let mensagem = response.message;
            
            // Tratamento específico para erros comuns
            if (response.status === 409) {
                mensagem = "Conflito de Dados!\n\n" + response.message + 
                "\n\nEste registro já existe no sistema ou há um conflito de informações.";
            } else if (response.status === 400) {
                mensagem = "Dados Inválidos!\n\n" + response.message;
            } else if (response.status === 404) {
                mensagem = "Não Encontrado!\n\n" + response.message;
            } else if (response.status === 500) {
                mensagem = "Erro no Servidor!\n\n" + response.message + 
                "\n\nPor favor, contate o administrador do sistema.";
            }
            
            // Adiciona informações extras se disponíveis
            if (response.error && response.error !== response.message) {
                mensagem += "\n\nTipo: " + response.error;
            }
            if (response.timestamp) {
                const data = new Date(response.timestamp).toLocaleString('pt-BR');
                mensagem += "\nHorário: " + data;
            }
            
            alert(mensagem);
        } else {
            alert("Erro desconhecido");
        }
    }

    // Função simples para excluir com tratamento de erro
    async function excluir(url, tipo) {
        const response = await setDelete(url);
        
        if (isSuccess(response)) {
            alert( tipo + " excluído com sucesso!");
            return true;
        } else {
            mostrarErro(response);
            return false;
        }
    }

