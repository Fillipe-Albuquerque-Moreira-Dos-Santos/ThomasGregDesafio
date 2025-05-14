const API_BASE_URL = 'http://localhost:8080';

function salvarCliente() {
    mostrarCarregamento(true);

    const formData = new FormData();
    const campos = ['nome', 'email'];

    // Validação e adição dos campos simples
    for (const campo of campos) {
        const valor = document.getElementById(`clienteForm:${campo}`).value;
        if (!valor) {
            mostrarMensagem("Preencha todos os campos obrigatórios!", 'erro');
            mostrarCarregamento(false);
            return;
        }
        formData.append(campo, valor);
    }

    // Obter ID do cliente (se existir)
    const clienteId = document.getElementById('clienteForm:clienteId').value;
    if (clienteId) {
        formData.append('id', clienteId);
    }

    let logradouros = [];
    try {
        const checkboxItems = document.querySelectorAll('#clienteForm\\:logradourosSelecionados_panel .ui-selectcheckboxmenu-item');

        logradourosSelecionados = Array.from(checkboxItems)
            .filter(item => item.querySelector('input[type="checkbox"]').checked)
            .map(item => {
                const label = item.textContent.trim();
                console.log("Label selecionado:", label);
                return label;
            });

        console.log("Logradouros selecionados:", logradourosSelecionados);

        // Se quiser enviar os valores ao backend:
        logradourosSelecionados.forEach(label => formData.append('logradouro', label));

    } catch (e) {
        console.error("Erro ao recuperar logradouros selecionados:", e);
    }
    const logotipoInput = document.querySelector('#clienteForm\\:logotipo');
    if (logotipoInput && logotipoInput.files.length > 0) {
        formData.append('file', logotipoInput.files[0]);
    }

    // Determinar método HTTP baseado se é criação ou atualização
    const metodo = clienteId ? 'PUT' : 'POST';
    const url = clienteId
        ? `${API_BASE_URL}/clientes/atualizar-cliente/${clienteId}`
        : `${API_BASE_URL}/clientes/salvar-cliente`;

// Enviar dados via API REST
    fetch(url, {
        method: metodo,
        body: formData,
        credentials: 'include'
    })
        .then(response => {
            // Verifica se a resposta foi bem-sucedida (status 2xx)
            if (!response.ok) {
                // Se não for, tenta extrair a mensagem de erro
                return response.json().then(err => {
                    throw new Error(err.message || 'Erro inesperado no servidor.');
                });
            }
            return response.json();
        })
        .then(() => {
            mostrarMensagem(clienteId ? "Cliente atualizado com sucesso!" : "Cliente cadastrado com sucesso!", 'sucesso');
            setTimeout(() => {
                window.location.href = "http://localhost:8081/cliente-frontend/listarClientes.xhtml";
            }, 1500);
        })
        .catch((error) => {
            console.error("Erro:", error);
            mostrarMensagem(error.message || "Erro ao " + (clienteId ? "atualizar" : "salvar") + " cliente", 'erro');
        })
        .finally(() => {
            mostrarCarregamento(false);
        });
}


function buscarClientes() {
    mostrarCarregamento(true);

    const username = 'usuario';
    const password = 'senha';
    const headers = new Headers();
    headers.set('Authorization', 'Basic ' + btoa(username + ':' + password));

    fetch(`${API_BASE_URL}/clientes/listar-todos`, {
        method: 'GET',
        headers: headers
    })
        .then(response => {
            if (!response.ok) {
                console.error('Status:', response.status);
                throw new Error('Erro ao buscar clientes');
            }
            return response.json();
        })
        .then(clientes => {
            atualizarTabelaClientes(clientes);
            if (clientes.length > 0) {
                mostrarMensagem(`${clientes.length} cliente(s) encontrado(s)`, 'info', 3000);
            } else {
                mostrarMensagem("Nenhum cliente encontrado", 'info');
            }
        })
        .catch(erro => {
            console.error("Erro ao buscar clientes:", erro);
            mostrarMensagem("Erro ao buscar clientes", 'erro');
        })
        .finally(() => {
            mostrarCarregamento(false);
        });
}

// Função para carregar dados do cliente para edição
function carregarDadosCliente() {
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    atualizarCliente(id);
}

function atualizarCliente(id) {
    mostrarCarregamento(true);

    id = id || document.getElementById('clienteForm:clienteId')?.value;

    // Se houver um ID, carregamos os dados do cliente para edição
    if (id) {
        // Primeiro carrega os logradouros disponíveis
        fetch(`${API_BASE_URL}/api/logradouros/listar`)
            .then(response => {
                if (!response.ok) throw new Error('Erro ao buscar logradouros em sua atualização');
                return response.json();
            })
            .then(logradouros => {
                if (logradouros && logradouros.length > 0) {
                    logradouros.forEach(log => {
                        if (log.hasOwnProperty('cliente')) {
                            delete log.cliente;
                        }
                    });
                }

                // Agora carrega os dados do cliente
                return fetch(`${API_BASE_URL}/clientes/carregar-cliente/${id}`);
            })
            .then(response => {
                if (!response.ok) throw new Error('Erro ao carregar cliente');
                return response.json();
            })
            .then(cliente => {
                // Preenche o formulário com os dados carregados
                document.getElementById('clienteForm:nome').value = cliente.nome || '';
                document.getElementById('clienteForm:email').value = cliente.email || '';
                document.getElementById('clienteForm:clienteId').value = id;

                // Remover a propriedade cliente dos logradouros do cliente para evitar erro de deserialização
                if (cliente.logradouros && cliente.logradouros.length > 0) {
                    cliente.logradouros.forEach(log => {
                        if (log.hasOwnProperty('cliente')) {
                            delete log.cliente;
                        }
                    });
                }

                // Selecionar os logradouros do cliente no componente PrimeFaces
                if (cliente.logradouros && cliente.logradouros.length > 0) {
                    setTimeout(() => {
                        try {
                            if (PF('logradourosSelecionados')) {
                                // Mapear os IDs dos logradouros do cliente
                                const logradouroIds = cliente.logradouros.map(log => log.id.toString());
                                console.log("Selecionando logradouros:", logradouroIds);
                                PF('logradourosSelecionados').selectItems(logradouroIds);
                            }
                        } catch (e) {
                            console.error("Erro ao selecionar logradouros:", e);
                        }
                    }, 500); // Pequeno delay para garantir que o componente já foi inicializado
                }

                // Muda o texto do botão para "Atualizar"
                const botao = document.querySelector('#clienteForm button[type="button"]');
                if (botao) {
                    botao.textContent = 'Atualizar Cliente';
                }

                mostrarMensagem("Dados do cliente carregados", 'info', 2000);
            })
            .catch(erro => {
                console.error("Erro ao carregar dados do cliente:", erro);
                mostrarMensagem("Erro ao carregar dados do cliente", 'erro');
            })
            .finally(() => {
                mostrarCarregamento(false);
            });
    }
}

function excluirCliente(id) {
    if (confirm("Tem certeza que deseja excluir este cliente?")) {
        mostrarCarregamento(true);

        fetch(`${API_BASE_URL}/clientes/excluir-cliente/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) throw new Error('Erro ao excluir cliente');
                return response.text();
            })
            .then(() => {
                mostrarMensagem("Cliente excluído com sucesso!", 'sucesso');
                // Recarregar a tabela
                buscarClientes();
            })
            .catch(erro => {
                console.error("Erro ao excluir cliente:", erro);
                mostrarMensagem("Erro ao excluir cliente", 'erro');
            })
            .finally(() => {
                mostrarCarregamento(false);
            });
    }
}

function atualizarTabelaClientes(clientes) {
    const tabela = document.getElementById('listForm:clientesTable_data');
    if (!tabela) return;

    tabela.innerHTML = '';

    if (clientes.length === 0) {
        tabela.innerHTML = '<tr><td colspan="6" class="texto-centralizado">Nenhum cliente encontrado.</td></tr>';
        return;
    }

    clientes.forEach(cliente => {
        const row = document.createElement('tr');

        const logradourosTexto = cliente.logradouros && cliente.logradouros.length > 0
            ? cliente.logradouros.join(', ')
            : 'Nenhum';


        row.innerHTML = `
            <td>${cliente.id}</td>
            <td>${cliente.nome}</td>
            <td>${cliente.email}</td>
            <td>${logradourosTexto}</td>
            <td style="text-align:center">
            ${cliente.logotipoUrl
            ?  `<img src="${cliente.logotipoUrl}" class="logo-preview" alt="Logo de ${cliente.nome}">`
                : `<div class="logo-placeholder">${cliente.nome.charAt(0).toUpperCase()}</div>`}
            </td>

            <td style="text-align:center">
                <button onclick="editarCliente(${cliente.id}); return false;" class="btn-editar">Editar</button>
                <button onclick="excluirCliente(${cliente.id}); return false;" class="btn-excluir">Excluir</button>
            </td>
        `;
        tabela.appendChild(row);
    });
}

function mostrarCarregamento(mostrar) {
    const loader = document.getElementById('loading-indicator');
    if (loader) {
        loader.style.display = mostrar ? 'flex' : 'none';
    }
}

function mostrarMensagem(texto, tipo, duracao = 4000) {
    const mensagemEl = document.getElementById('mensagem-flutuante');
    const mensagemTexto = document.getElementById('mensagem-texto');

    if (mensagemEl && mensagemTexto) {
        // Remover classes anteriores
        mensagemEl.className = 'mensagem-flutuante';
        // Adicionar classe de tipo
        mensagemEl.classList.add(tipo);

        // Definir texto
        mensagemTexto.textContent = texto;

        // Mostrar mensagem
        mensagemEl.style.display = 'flex';

        // Auto-fechar após duração
        setTimeout(() => {
            fecharMensagem();
        }, duracao);
    } else {
        // Fallback se os elementos não existirem
        alert(texto);
    }
}

function fecharMensagem() {
    const mensagemEl = document.getElementById('mensagem-flutuante');
    if (mensagemEl) {
        mensagemEl.style.display = 'none';
    }
}

function editarCliente(id) {
    window.location.href = `cadastro.xhtml?id=${id}`;
}

function limparCampos() {
    document.getElementById('clienteForm').reset();

    // Limpar também o ID oculto
    const idInput = document.getElementById('clienteForm:clienteId');
    if (idInput) {
        idInput.value = '';
    }

    // Limpar seleção de logradouros
    if (PF('logradourosSelecionados')) {
        PF('logradourosSelecionados').selectItems([]);
    }

    // Restaurar texto do botão
    const botao = document.querySelector('#clienteForm button[type="button"]');
    if (botao) {
        botao.textContent = 'Salvar Cliente';
    }
}

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    // Verificar se estamos na página de listagem
    if (document.getElementById('listForm:clientesTable_data')) {
        buscarClientes();
    }
    // Verificar se estamos na página de cadastro/edição
    else if (document.getElementById('clienteForm')) {
        carregarDadosCliente();
    }
});