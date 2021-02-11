Feature: Orders view panel

Scenario: Order made and panel contain its positions
  Given Created food type with name "Блины с мясом"
  And Registered table with number 1
  When Started party for single person
  And Move to cart "Блины с мясом" with amount 2
  And Making order
  Then Orders panel contains 2 order position for "Блины с мясом" with scheduled status

Scenario: Order made and some positions started to preparing so panel reflects it
  Given Created food type with name "Блины с мясом"
  And Registered table with number 1
  When Started party for single person
  And Move to cart "Блины с мясом" with amount 2
  And Making order
  And Changing status 1 of "Блины с мясом" on preparing
  Then Orders panel contains 1 order position for "Блины с мясом" with scheduled status
  And Orders panel contains 1 order position for "Блины с мясом" with preparing status

Scenario: Order made and some positions delivered so it removed from panel
  Given Created food type with name "Блины с мясом"
  And Registered table with number 1
  When Started party for single person
  And Move to cart "Блины с мясом" with amount 2
  And Making order
  And Changing status 1 of "Блины с мясом" on completed
  Then Orders panel contains 1 order position for "Блины с мясом" with scheduled status