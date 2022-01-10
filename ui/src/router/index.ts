import { createRouter, createWebHashHistory } from 'vue-router'
import FileEditor from '@/containers/FileEditor.vue'
import Logs from '@/containers/Logs.vue'

const router = createRouter({
  history: createWebHashHistory('/opennms/ui'),
  routes: [
    {
      path: '/',
      name: 'nodes',
      component: () => import('@/containers/Nodes.vue')
    },
    {
      path: '/node/:id',
      name: 'Node Details',
      component: () => import('@/containers/NodeDetails.vue')
    },
    {
      path: '/inventory',
      name: 'Inventory',
      component: () => import('@/containers/Inventory.vue'),
      children: [
        {
          path: '',
          component: () => import('@/components/Inventory/StepAdd.vue')
        },
        {
          path: 'configure',
          component: () => import('@/components/Inventory/StepConfigure.vue')
        },
        {
          path: 'schedule',
          component: () => import('@/components/Inventory/StepSchedule.vue')
        }
      ]
    },
    {
      path: '/file-editor',
      name: 'FileEditor',
      component: FileEditor
    },
    {
      path: '/logs',
      name: 'Logs',
      component: Logs
    },
    {
      path: '/map',
      name: 'Map',
      component: () => import('@/containers/Map.vue'),
      children: [
        {
          path: '',
          name: 'MapAlarms',
          component: () => import('@/components/Map/MapAlarmsGrid.vue')
        },
        {
          path: 'nodes',
          name: 'MapNodes',
          component: () => import('@/components/Map/MapNodesGrid.vue')
        }
      ]
    },
    {
      path: '/open-api',
      name: 'OpenAPI',
      component: () => import('@/containers/OpenAPI.vue')
    },
    {
      path: '/resource-graphs',
      name: 'ResourceGraphs',
      component: () => import('@/containers/ResourceGraphs.vue'),
      children: [
        {
          path: '',
          name: 'Resources',
          component: () => import('@/components/Resources/Resources.vue')
        },
        {
          path: 'graphs',
          name: 'Graphs',
          component: () => import('@/components/Resources/Graphs.vue')
        }       
      ]
    },
    {
      path: '/:pathMatch(.*)*', // catch other paths and redirect
      redirect: '/'
    }
  ]
})

export default router
