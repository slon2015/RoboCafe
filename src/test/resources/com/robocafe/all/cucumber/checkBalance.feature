Feature: Balance
  balance observability

Scenario: Zero balance on session start
  Given Registered table with number 1
  When Started party for single person
  Then Balance of person 1 on table 1 equals 0

Scenario: Balance updates after making order
  Given Registered table with number 1
  When Started party for single person
  And Move to cart "Блины с мясом" with amount 1
  And Making order
  Then Balance of person 1 on table 1 equals 150

Scenario: Balance correctness after reloading
  Given Registered table with number 1
  When Started party for single person
  And Move to cart "Блины с мясом" with amount 1
  And Making order
  And Table app reloaded
  Then Balance of person 1 on table 1 equals 150