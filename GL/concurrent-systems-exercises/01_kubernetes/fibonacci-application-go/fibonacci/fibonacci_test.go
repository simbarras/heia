package fibonacci

import (
	"testing"
	"math/big"
)

func TestFibonacci(t *testing.T) {
	t.Log("Test Fibonacci")
	// Input tab for testing
	entry := []int{0, 1, 2, 3, 4, 5, 10, 12, 50}
	// Expected output tab for testing
	expected := []int64{0, 1, 1, 2, 3, 5, 55, 144, 12586269025}

	for i, v := range entry {
		res, _ := Fast_Fibonacci(v)
		if res.Cmp(big.NewInt(expected[i])) != 0 {
			t.Errorf("Fibonacci(%d) = %d, expected %d", v, res, expected[i])
		}
	}
}

func TestS2i(t *testing.T) {
	t.Log("Test S2i")
	// Input tab for testing
	entry := []string{"0", "1", "2", "3", "4", "5", "10", "12", "50", "a", "1a", "a1"}
	// Expected output tab for testing
	expected := []int{0, 1, 2, 3, 4, 5, 10, 12, 50, -1, -1, -1}

	for i, v := range entry {
		res := S2i(v)
		if res != expected[i] {
			t.Errorf("S2i(%s) = %d, expected %d", v, res, expected[i])
		}
	}
}