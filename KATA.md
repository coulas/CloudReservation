# Objectifs du Kata

## Refacto l'application pour valider [les 12 factors](https://12factor.net/fr)

- Renvoyer les logs vers sortie standard ou la sortie d’erreur 
- Externaliser la configuration (utiliser les variables env, …) 
- Optionnel Livrer avec Docker (et Jib)

Hors Kata : 
- Passer tout les services tiers sur des URL 
- faire un fat Jar
- Mettre chaque livrable dans une base de code indépendante
- Passer toutes les tâches de maintenance en worker indépendants

## Choisir une cible cloud

- Quel fournisseur ?

    1. **AWS**
    1. Azure
    1. Google CP
    1. IBM CP
    1. Oracle CP
    1. OVH Cloud

- Quel niveau de service ?

    - un (fat ou non) jar déployé sur une VM statique
        - vous le faites déjà dans votre infra
    - **un fat jar managé par Beanstalk** (si on peut déployer 3 VM sur un EC2 ?, sinon, 3 fat jar sur un EC2)
        - la solution facile pour une infra elastique
        - exposer une health api ?
    - un conteneur déployé sur une VM statique
        - dommage de ne pas utiliser un service managé pour un conteneur
    - un conteneur managé par beanstalk
        - un mélange des autres options
    - **un conteneur déployé dans ECS (conteneurs managés)**
        - la solution de gestion de docker 
        - exposer une health api ?
    - un conteneur déployé dans EKS (Kubernetes managé)
        - encore en cours d'évolutions fortes
    - tout ou partie en serverless
        - pour un jour suivant

## Quelles startégies de migration ?
Composants feuilles (Bookingreference et TrainData) en serverless 

## Une roadmap :
1. BookingReference en serverLess
1. TrainData (en Fat Jar / beanStalk)
1. TrainData qui stocke les trains en RDS ou Dynamo
1. TrainReservation en docker qui expose une page html
1. la page html pointe sur des images/JS sur S3
1. TrainResa en ECS

