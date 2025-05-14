// Variável para armazenar ID durante edição
let logradouroIdEmEdicao = null;

// Executa quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM carregado. Iniciando busca de logradouros...");
    buscarLogradouros();
});

// Função para buscar logradouros da API
function buscarLogradouros() {
    console.log("Executando buscarLogradouros...");
    fetch('http://localhost:8080/api/logradouros/listar')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(logradouros => {
            console.log("Logradouros recebidos:", logradouros);
            preencherTabelaLogradouros(logradouros);
        })
        .catch(error => {
            console.error("Erro ao buscar logradouros:", error);
            mostrarMensagem("Erro ao buscar logradouros: " + error.message, "erro");
        });
}

// Função para preencher a tabela com os logradouros
function preencherTabelaLogradouros(logradouros) {
    const tabela = document.getElementById('corpoTabelaLogradouros');
    tabela.innerHTML = '';

    if (!logradouros || logradouros.length === 0) {
        tabela.innerHTML = '<tr><td colspan="3">Nenhum logradouro encontrado.</td></tr>';
        return;
    }

    logradouros.forEach(log => {
        const row = document.createElement('tr');

        // Escape HTML antes de inserir para evitar problemas com aspas e caracteres especiais
        const logradouroEscapado = escapeHtml(log.logradouro);

        row.innerHTML = `
            <td>${log.id}</td>
            <td>${logradouroEscapado}</td>
            <td>
                <button type="button" onclick="editarLogradouro(${log.id}, '${logradouroEscapado.replace(/'/g, "\\'")}')" 
                        class="btn btn-primary btn-sm">Editar</button>
                <button type="button" onclick="confirmarExclusao(${log.id})" 
                        class="btn btn-danger btn-sm">Excluir</button>
            </td>
        `;
        tabela.appendChild(row);
    });
}



// Função para escapar HTML e evitar XSS
function escapeHtml(text) {
    if (!text) return '';

    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Função para abrir o diálogo de novo logradouro
function abrirDialogNovo() {
    document.querySelector('#formDialog\\:descricao').value = '';
    logradouroIdEmEdicao = null;
    PF('dlgLogradouro').show();
}

// Função para editar um logradouro existente
function editarLogradouro(id, descricao) {
    try {
        document.querySelector('#formDialog\\:descricao').value = descricao;
        logradouroIdEmEdicao = id;
        PF('dlgLogradouro').show();
    } catch (error) {
        console.error("Erro ao editar logradouro:", error);
        mostrarMensagem("Erro ao abrir formulário de edição", "erro");
    }
}

// Função para confirmar exclusão
function confirmarExclusao(id) {
    if (confirm('Deseja realmente excluir este logradouro?')) {
        excluirLogradouro(id);
    }
}


async function excluirLogradouro(id) {
    try {
        const response = await fetch(`http://localhost:8080/api/logradouros/excluir/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            // Tenta extrair o JSON com a chave "message"
            const erroJson = await response.json();
            const mensagemErro = erroJson.message || "Erro ao excluir logradouro.";
            throw new Error(mensagemErro);
        }
        mostrarMensagem("Logradouro excluído com sucesso", "sucesso");
        setTimeout(() => {
            buscarLogradouros();
        }, 1);

    } catch (error) {
        console.error("Erro ao excluir logradouro:", error);
        mostrarMensagem(error.message, "erro"); // Mostra só o campo "message"
    }
}





// Função para salvar um logradouro (novo ou editado)
function salvarLogradouro() {
    const descricao = document.querySelector('#formDialog\\:descricao').value.trim();

    if (!descricao) {
        mostrarMensagem("Por favor, preencha a descrição do logradouro", "aviso");
        return;
    }

    const logradouro = {
        logradouro: descricao
    };

    // Se estiver editando, inclui o ID
    if (logradouroIdEmEdicao) {
        logradouro.id = logradouroIdEmEdicao;
    }

    const url = logradouroIdEmEdicao
        ? ` http://localhost:8080/api/logradouros/editar/${logradouroIdEmEdicao}`
        : 'http://localhost:8080/api/logradouros/salvar';

    const method = logradouroIdEmEdicao ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(logradouro)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(() => {
            const mensagem = logradouroIdEmEdicao
                ? "Logradouro atualizado com sucesso"
                : "Logradouro salvo com sucesso";

            mostrarMensagem(mensagem, "sucesso");
            PF('dlgLogradouro').hide();
            buscarLogradouros(); // Atualiza a tabela
        })
        .catch(error => {
            console.error("Erro ao salvar logradouro:", error);
            mostrarMensagem("Erro ao salvar logradouro: " + error.message, "erro");
        });
}

// Função para mostrar mensagens utilizando PrimeFaces
function mostrarMensagem(texto, tipo) {
    const severity =
        tipo === "erro" ? "error" :
            tipo === "aviso" ? "warn" :
                tipo === "info" ? "info" :
                    "success";

    // Tenta usar o PrimeFaces growl se disponível
    if (typeof PF !== 'undefined' && PF('growl')) {
        PF('growl').renderMessage({
            summary: tipo.charAt(0).toUpperCase() + tipo.slice(1),
            detail: texto,
            severity: severity
        });
    } else {
        // Fallback para alert se PrimeFaces não estiver disponível
        alert(texto);
    }
}

// Função para debug
function debug(info) {
    const debugElement = document.getElementById('debugInfo');
    if (debugElement) {
        debugElement.innerHTML += `<p>${info}</p>`;
    }
    console.log(info);
}