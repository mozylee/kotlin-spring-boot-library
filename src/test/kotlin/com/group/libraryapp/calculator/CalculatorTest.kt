package com.group.libraryapp.calculator

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito
import org.mockito.BDDMockito.willThrow

class CalculatorTest {

    @Test
    @DisplayName("add: 정상 케이스")
    fun add() {
        // given
        val initValue = 5
        val operand = 5
        val calculator = Calculator(initValue)

        // when
        calculator.add(operand)

        // then
        assertThat(calculator.number).isEqualTo(initValue + operand)
    }

    @Test
    @DisplayName("minus: 정상 케이스")
    fun minus() {
        // given
        val initValue = 5
        val operand = 5
        val calculator = Calculator(initValue)

        // when
        calculator.minus(operand)

        // then
        assertThat(calculator.number).isEqualTo(initValue - operand)
    }

    @Test
    @DisplayName("multiply: 정상 케이스")
    fun multiply() {
        // given
        val initValue = 5
        val operand = 5
        val calculator = Calculator(initValue)

        // when
        calculator.multiply(operand)

        // then
        assertThat(calculator.number).isEqualTo(initValue * operand)
    }

    @Test
    @DisplayName("divide: 정상 케이스")
    fun divide() {
        // given
        val initValue = 5
        val operand = 5
        val calculator = Calculator(initValue)

        // when
        calculator.divide(operand)

        // then
        assertThat(calculator.number).isEqualTo(initValue / operand)
    }

    @Test
    @DisplayName("divide: 피연산자가 0인 경우 IllegalArgumentException")
    fun divide_when_error() {
        // given
        val initValue = 5
        val operand = 0
        val calculator = Calculator(initValue)

        // when
        val message = assertThrows<IllegalArgumentException> { calculator.divide(operand) }.message

        // then
        assertThat(message).isEqualTo("Can not divided by 0")
    }

}