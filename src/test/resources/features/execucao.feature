# language: pt
Funcionalidade: Fila de Execução
  Como o sistema de execução
  Quero gerenciar a fila de serviços das OS
  Para controlar o fluxo de reparos

  Cenário: Adicionar OS na fila ao receber pagamento confirmado
    Dado que o pagamento foi confirmado para a OS "os-exec-001"
    Quando o Execution Service processa o evento de pagamento
    Então a OS é adicionada à fila com status "NA_FILA"

  Cenário: Fluxo completo de execução
    Dado que a OS "os-exec-002" está na fila de execução
    Quando o técnico "Mecanico Jose" inicia o diagnóstico
    E o técnico inicia o reparo
    E o técnico finaliza a OS
    Então a OS fica com status "FINALIZADO"
    E o evento "execucao.finalizada" é publicado
