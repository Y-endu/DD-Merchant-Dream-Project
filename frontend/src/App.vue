<template>
  <div style="font-family: Arial, sans-serif; padding: 2rem;">
    <h1>DD Merchant Demo</h1>
    <p>Backend status: <strong>{{ status }}</strong></p>
    <button @click="check">Check health</button>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'App',
  data() {
    return {
      status: 'unknown'
    }
  },
  created() {
    this.check()
  },
  methods: {
    async check() {
      try {
        const res = await axios.get('/api/health')
        this.status = res.data.status || JSON.stringify(res.data)
      } catch (e) {
        this.status = 'error (' + (e.message || e) + ')'
      }
    }
  }
}
</script>
