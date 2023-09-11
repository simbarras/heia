package fibonacci

import (
	"math/big"
)

const AsciiOffset = 48

// Tiniest string to int implementation
func S2i(s string) int {
	var (
		res  = 0
		base = 1
	)
	for i := len(s) - 1; i >= 0; i-- {
		digit := int(s[i]) - AsciiOffset
		if digit < 0 || digit > 9 {
			return -1
		}
		res += (int(s[i]) - AsciiOffset) * base
		base *= 10
	}
	return res
}

// Inspired by: https://www.nayuki.io/page/fast-fibonacci-algorithms
func Fast_Fibonacci(i int) (*big.Int, *big.Int) {
	if i == 0 {
		return big.NewInt(0), big.NewInt(1)
	} else {
		a, b := Fast_Fibonacci(i / 2)
		c := big.NewInt(0)
		d := big.NewInt(0)
		tmp := big.NewInt(2)
		c.Mul(a, tmp.Mul(tmp, b).Sub(tmp, a))
		d.Mul(a, a).Add(d, b.Mul(b, b))
		if i%2 == 0 {
			return c, d
		} else {
			return d, c.Add(c, d)
		}
	}

}

type FibonacciResponse struct {
	Index  int      `json:"index"`
	Result *big.Int `json:"result"`
}
