# language: pt

@web @home
Funcionalidade: Carregamento da Home do Blog do Agi
  Como um usuário do Blog do Agi
  Eu quero acessar a página inicial
  Para verificar que o blog carrega corretamente e está acessível

  @smoke
  Cenário: Acessar a Home do Blog do Agi com sucesso
    Dado que acesso a Home do Blog do Agi
    Então a página deve ser carregada com sucesso
    E o título da página deve conter "Agi"
    E a URL deve corresponder ao endereço do blog

  @smoke
  Cenário: Verificar elementos estruturais da Home
    Dado que acesso a Home do Blog do Agi
    Então o header da página deve estar visível
    E o menu de navegação deve estar visível
    E o conteúdo principal deve estar visível

#  @bug @home
#  Cenário: Ícone de busca ausente na Home devido a bug de layout
    # BUG IDENTIFICADO: A Home page possui problema de layout que impede
    # a renderização do ícone de busca (lupa) no canto superior direito.
    # O mesmo ícone funciona corretamente em todas as páginas internas do blog.
    # Este cenário documenta o comportamento atual (buggy) da Home.
    # Quando o bug for corrigido, este cenário deve ser atualizado para @smoke.
#    Dado que acesso a Home do Blog do Agi
#    Então o ícone de busca NÃO deve estar visível na Home
