## Comandos de Compilação e Execução:

### 1. Compilar o código:
```bash
javac -d bin src/*.java
```

### 2. Executar o servidor R (Receptor):
```bash
java -cp bin R
```

### 3. Executar o cliente Distribuidor:
```bash
java -cp bin Distribuidor
```

## O que aconteceu na demonstração:

**Servidor R (Receptor):**
- ✅ Iniciou corretamente na porta 12345
- ✅ Detectou 20 processadores disponíveis
- ✅ Aceitou múltiplas conexões do cliente
- ✅ Recebeu e processou ComunicadoEncerramento de cada conexão

**Cliente Distribuidor:**
- ✅ Conectou com sucesso ao servidor R
- ✅ Enviou ComunicadoEncerramento para todos os servidores
- ⚠️ Teve erro na entrada de dados (esperava número inteiro)

## Demonstração do Comportamento com Múltiplos Clientes:

O que vimos nos logs mostra exatamente o comportamento descrito no documento:

1. **Comportamento Sequencial**: O servidor R atende um cliente por vez
2. **Múltiplas Conexões**: Cada cliente cria uma nova conexão
3. **Processamento Individual**: Cada conexão é processada separadamente
4. **Encerramento Limpo**: Cada cliente envia ComunicadoEncerramento

Para testar com dados reais, você pode executar:
```bash
echo "1000000" | java -cp bin Distribuidor
```

Isso enviará um vetor de 1 milhão de elementos para o servidor R processar.


### Relatos dos integrantes


####  Matheus:

O desenvolvimento deste projeto nos trouxe vários desafios, posso citar como sendo os principais: o tempo para a realização do projeto, tivemos que manejar bem as tarefas para cada um, caso contrário não teríamos tempo para acabar o projeto; a integração do trabalho que cada um fez, que as vezes gerou certa incompatibilidade devido a por exemplo, nomes de variáveis; a interpretação do enunciado em geral nos trouxe certas confusões, como por exemplo na obtenção dos IPs dos computadores através do IP config, em que não sabíamos ao certo no momento qual era o IP que deveria ser utilizado para deixar hard-coded no projeto. Por fim, os testes foram muito úteis para eliminar dúvidas e até mesmos erros escondidos do projeto, como por exemplo os servidores aceitarem mais de uma máquina ao mesmo tempo.

#### Manoel: 

A implementação da comunicação básica com Sockets foi um ótimo exercício sobre arquitetura cliente-servidor. No entanto, a verdadeira complexidade surgiu ao introduzir Threads para gerenciar a concorrência. Garantir que múltiplas tarefas pudessem executar em paralelo, acessando recursos de forma segura e sem conflitos, exigiu um estudo aprofundado sobre sincronização.
​Foi um aprendizado intenso que solidificou meus conhecimentos sobre os desafios práticos da programação concorrente e da comunicação em rede.


#### Beatriz:

Dentre os desafios enfrentados no projeto, dou ênfase na fase de integração do código, que foi onde realmente juntamos as peças de todo o time. Essa parte exigiu uma dedicação extra, especialmente no debugging durante os testes, que foi fundamental para conseguirmos aplicar os patches de correção e garantir que o sistema ficasse robusto. Achei super interessante e um ótimo aprendizado a forma como aplicamos a divisão de tarefas utilizando o conceito de paralelismo. Ver a performance melhorar com essa otimização foi bem gratificante, e o fato de termos usado Java facilitou muito, já que é uma linguagem que adoro trabalhar. O projeto era complexo, não era nada simples, mas era exatamente esse o desafio. Ter conseguido entregar e fazer tudo funcionar perfeitamente me deu uma sensação de competência muito grande. Fiquei feliz e com o sentimento de que evoluí muito.
