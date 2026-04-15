# language: pt

@web @search
Funcionalidade: Pesquisa de artigos do Blog do Agi
  Como um usuário do Blog do Agi
  Eu quero utilizar a pesquisa de artigos
  Para encontrar conteúdos relevantes sobre finanças e produtos do Agibank

  Contexto:
    # O ícone de busca funciona corretamente nas páginas internas do blog.
    # Navegamos para uma página interna antes de cada cenário de busca.
    Dado que estou em uma página interna do blog
    Então o ícone de busca deve estar visível no header

  @smoke
  Cenário: Busca por termo válido exibe resultados relevantes
    # Cenário principal: fluxo completo de busca com resultado esperado.
    # Valida o funcionamento end-to-end da funcionalidade de pesquisa.
    Quando clico no ícone de busca
    Então o overlay de busca deve ser exibido
    E o campo de busca deve estar visível e focado
    Quando digito o termo de busca "emprestimo"
    E submeto a busca
    Então devo ser redirecionado para a página de resultados
    E os resultados de busca devem ser exibidos
    E os resultados devem conter artigos relacionados ao termo pesquisado

  @smoke
  Cenário: Busca por termo inexistente exibe mensagem informativa ao usuário
  # Valida que o sistema informa claramente que nenhum resultado foi encontrado,
  # exibindo o termo pesquisado no título e uma mensagem orientativa ao usuário.
    Quando clico no ícone de busca
    Então o overlay de busca deve ser exibido
    Quando digito o termo de busca "xyzqwerty123456789"
    E submeto a busca
    Então devo ser redirecionado para a página de resultados
    E o título da página de resultados deve conter o termo pesquisado "xyzqwerty123456789"
    E a mensagem de nenhum resultado deve conter "nada foi encontrado para sua pesquisa"

  @smoke
  Cenário: Fechar o overlay de busca sem pesquisar mantém o usuário na página
    # Valida que o usuário pode cancelar a busca sem efeitos colaterais.
    Quando clico no ícone de busca
    Então o overlay de busca deve ser exibido
    Quando fecho o overlay de busca
    Então o overlay de busca não deve estar visível
